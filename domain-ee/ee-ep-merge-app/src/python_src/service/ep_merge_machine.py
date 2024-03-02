import asyncio
import logging
from enum import Enum
from typing import Type

from fastapi.encoders import jsonable_encoder
from hoppy.async_hoppy_client import AsyncHoppyClient
from hoppy.exception import ResponseException
from pydantic import ValidationError
from config import EP_MERGE_SPECIAL_ISSUE_CODE
from schema import (
    add_claim_note,
    cancel_claim,
    create_contentions,
    get_claim,
    get_contentions,
    update_contentions,
)
from schema import update_temp_station_of_jurisdiction as tsoj
from schema.merge_job import JobState, MergeJob
from schema.request import GeneralRequest
from schema.response import GeneralResponse
from service.hoppy_service import HOPPY, ClientName
from service.job_store import JOB_STORE
from statemachine import State, StateMachine
from util.contentions_util import ContentionsUtil
from util.metric_logger import distribution, increment

ERROR_STATES_TO_LOG_METRICS = [
    JobState.CANCEL_EP400_CLAIM,
    JobState.CANCEL_CLAIM_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION,
    JobState.ADD_CLAIM_NOTE_TO_EP400,
]

JOB_SUCCESS_METRIC = 'job.success'
JOB_FAILURE_METRIC = 'job.failure'
JOB_ERROR_METRIC_PREFIX = 'job.error'
JOB_DURATION_METRIC = 'job.duration'
JOB_SKIPPED_MERGE_METRIC = 'job.skipped_merge'
JOB_NEW_CONTENTIONS_METRIC = 'job.new_contentions'

CANCEL_TRACKING_EP = "60"
CANCELLATION_REASON_FORMAT = "Issues moved into or confirmed in pending EP{ep_code} - claim #{claim_id}"


class Workflow(str, Enum):
    PROCESS = 'process'
    RESTART = ('resume_restart',)
    RESUME_MOVE_CONTENTIONS = ('resume_processing_from_running_move_contentions_to_pending_claim',)
    RESUME_CANCEL_EP400 = ('resume_processing_from_running_cancel_ep400_claim',)
    RESUME_ADD_NOTE = 'resume_processing_from_running_add_note_to_ep400_claim'


class EpMergeMachine(StateMachine):
    job: MergeJob | None = None
    cancellation_reason: str | None = None
    original_tsoj: str | None = None
    num_new_contentions: int | None = None
    skipped_merge: bool = True

    # States:
    pending = State(initial=True, value=JobState.PENDING)
    running_get_pending_claim = State(value=JobState.GET_PENDING_CLAIM)
    running_get_pending_claim_failed_remove_special_issue = State(value=JobState.GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE)
    running_get_pending_contentions = State(value=JobState.GET_PENDING_CLAIM_CONTENTIONS)
    running_get_pending_contentions_failed_remove_special_issue = State(value=JobState.GET_PENDING_CLAIM_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE)
    running_get_ep400_contentions = State(value=JobState.GET_EP400_CLAIM_CONTENTIONS)
    running_set_temp_station_of_jurisdiction = State(value=JobState.SET_TEMP_STATION_OF_JURISDICTION)
    running_set_temp_station_of_jurisdiction_failed_remove_special_issue = State(value=JobState.SET_TEMP_STATION_OF_JURISDICTION_FAILED_REMOVE_SPECIAL_ISSUE)
    running_merge_contentions = State(value=JobState.MERGE_CONTENTIONS)
    running_move_contentions_to_pending_claim = State(value=JobState.MOVE_CONTENTIONS_TO_PENDING_CLAIM)
    running_move_contentions_failed_remove_special_issue = State(value=JobState.MOVE_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE)
    running_move_contentions_failed_revert_temp_station_of_jurisdiction = State(value=JobState.MOVE_CONTENTIONS_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION)
    running_cancel_ep400_claim = State(value=JobState.CANCEL_EP400_CLAIM)
    running_cancel_claim_failed_revert_temp_station_of_jurisdiction = State(value=JobState.CANCEL_CLAIM_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION)
    running_add_claim_note_to_ep400 = State(value=JobState.ADD_CLAIM_NOTE_TO_EP400)
    completed_success = State(final=True, value=JobState.COMPLETED_SUCCESS)
    completed_error = State(final=True, value=JobState.COMPLETED_ERROR)

    process = (
        pending.to(running_get_pending_claim)
        | running_get_pending_claim.to(running_get_pending_contentions, unless="has_error")
        | running_get_pending_claim.to(running_get_pending_claim_failed_remove_special_issue, cond="has_error")
        | running_get_pending_claim_failed_remove_special_issue.to(completed_error)
        | running_get_pending_contentions.to(running_get_ep400_contentions, unless="has_error")
        | running_get_pending_contentions.to(running_get_pending_contentions_failed_remove_special_issue, cond="has_error")
        | running_get_pending_contentions_failed_remove_special_issue.to(completed_error)
        | running_get_ep400_contentions.to(running_set_temp_station_of_jurisdiction, unless="has_error")
        | running_get_ep400_contentions.to(completed_error, cond="has_error")
        | running_set_temp_station_of_jurisdiction.to(running_merge_contentions, cond="has_new_contentions", unless="has_error")
        | running_set_temp_station_of_jurisdiction.to(running_cancel_ep400_claim, unless=["has_new_contentions", "has_error"])
        | running_set_temp_station_of_jurisdiction.to(running_set_temp_station_of_jurisdiction_failed_remove_special_issue, cond="has_error")
        | running_set_temp_station_of_jurisdiction_failed_remove_special_issue.to(completed_error)
        | running_merge_contentions.to(running_move_contentions_to_pending_claim, unless="has_error")
        | running_merge_contentions.to(completed_error, cond="has_error")
        | running_move_contentions_to_pending_claim.to(running_cancel_ep400_claim, unless="has_error")
        | running_move_contentions_to_pending_claim.to(running_move_contentions_failed_remove_special_issue, cond="has_error")
        | running_move_contentions_failed_remove_special_issue.to(running_move_contentions_failed_revert_temp_station_of_jurisdiction)
        | running_move_contentions_failed_revert_temp_station_of_jurisdiction.to(completed_error)
        | running_cancel_ep400_claim.to(running_add_claim_note_to_ep400, unless="has_error")
        | running_cancel_ep400_claim.to(running_cancel_claim_failed_revert_temp_station_of_jurisdiction, cond="has_error")
        | running_cancel_claim_failed_revert_temp_station_of_jurisdiction.to(completed_error)
        | running_add_claim_note_to_ep400.to(completed_success, unless="has_error")
        | running_add_claim_note_to_ep400.to(completed_error, cond="has_error")
    )
    resume_restart = process
    resume_processing_from_running_cancel_ep400_claim = (
        pending.to(running_get_pending_claim)
        | running_get_pending_claim.to(running_cancel_ep400_claim, unless="has_error")
        | running_get_pending_claim.to(running_get_pending_claim_failed_remove_special_issue, cond="has_error")
        | running_get_pending_claim_failed_remove_special_issue.to(completed_error)
        | running_cancel_ep400_claim.to(running_add_claim_note_to_ep400, unless="has_error")
        | running_cancel_ep400_claim.to(running_cancel_claim_failed_revert_temp_station_of_jurisdiction, cond="has_error")
        | running_cancel_claim_failed_revert_temp_station_of_jurisdiction.to(completed_error)
        | running_add_claim_note_to_ep400.to(completed_success, unless="has_error")
        | running_add_claim_note_to_ep400.to(completed_error, cond="has_error")
    )
    resume_processing_from_running_add_note_to_ep400_claim = (
        pending.to(running_get_pending_claim)
        | running_get_pending_claim.to(running_add_claim_note_to_ep400, unless="has_error")
        | running_get_pending_claim.to(running_get_pending_claim_failed_remove_special_issue, cond="has_error")
        | running_get_pending_claim_failed_remove_special_issue.to(completed_error)
        | running_add_claim_note_to_ep400.to(completed_success, unless="has_error")
        | running_add_claim_note_to_ep400.to(completed_error, cond="has_error")
    )

    def __init__(self, merge_job: MergeJob, main_event: Workflow = Workflow.PROCESS):
        self.job = merge_job
        self.main_event = main_event
        super().__init__()

    def start(self):
        self.send(self.main_event.value)

    def on_transition(self, event, source, target):
        logging.info(f"event=jobTransition trigger={event} job_id={self.job.job_id} old={source.value} new={target.value}")
        self.job.update(target.value)
        JOB_STORE.update_merge_job(self.job)

    @pending.exit
    def on_start_process(self, event):
        if event == self.process.name:
            logging.info(f"event=jobStarted trigger={event} job_id={self.job.job_id}")
        else:
            logging.info(f"event=jobResumed trigger={event} job_id={self.job.job_id} starting_state={self.job.state}")

    @running_get_pending_claim.enter
    def on_get_pending_claim(self, event):
        request = get_claim.Request(claim_id=self.job.pending_claim_id)
        response = self.make_request(request=request, hoppy_client=HOPPY.get_client(ClientName.GET_CLAIM), response_type=get_claim.Response)

        if response is not None and response.status_code == 200:
            if response.claim is None or response.claim.end_product_code is None:
                logging.info(self.job.state)
                self.add_error(f"Pending claim #{self.job.pending_claim_id} does not have an end product code")
            else:
                self.cancellation_reason = CANCELLATION_REASON_FORMAT.format(ep_code=response.claim.end_product_code, claim_id=self.job.pending_claim_id)
            self.original_tsoj = response.claim.temp_station_of_jurisdiction

        self.send(event=event)

    @running_get_pending_contentions.enter
    def on_get_pending_contentions(self, event):
        request = get_contentions.Request(claim_id=self.job.pending_claim_id)
        response = self.make_request(request=request, hoppy_client=HOPPY.get_client(ClientName.GET_CLAIM_CONTENTIONS), response_type=get_contentions.Response)
        self.send(event=event, pending_contentions_response=response)

    @running_get_ep400_contentions.enter
    def on_get_ep400_contentions(self, event, pending_contentions_response=None):
        request = get_contentions.Request(claim_id=self.job.ep400_claim_id)
        response = self.make_request(
            request=request,
            hoppy_client=HOPPY.get_client(ClientName.GET_CLAIM_CONTENTIONS),
            response_type=get_contentions.Response,
            expected_statuses=[200, 204],
        )
        self.send(event=event, pending_contentions_response=pending_contentions_response, ep400_contentions_response=response)

    @running_set_temp_station_of_jurisdiction.enter
    def on_set_temp_station_of_jurisdiction(self, event, pending_contentions_response=None, ep400_contentions_response=None):
        request = tsoj.Request(temp_station_of_jurisdiction="398", claim_id=self.job.ep400_claim_id)
        self.make_request(request=request, hoppy_client=HOPPY.get_client(ClientName.PUT_TSOJ), response_type=tsoj.Response)
        self.send(event=event, pending_contentions_response=pending_contentions_response, ep400_contentions_response=ep400_contentions_response)

    @running_merge_contentions.enter
    def on_merge_contentions(self, event, pending_contentions_response=None, ep400_contentions_response=None):
        self.skipped_merge = False
        new_contentions = None
        try:
            new_contentions = ContentionsUtil.new_contentions(pending_contentions_response.contentions, ep400_contentions_response.contentions)
        except Exception as e:
            self.add_error(e.message)
        self.send(event=event, new_contentions=new_contentions, ep400_contentions_response=ep400_contentions_response)

    @running_move_contentions_to_pending_claim.enter
    def on_move_contentions_to_pending_claim(self, event, new_contentions=None, ep400_contentions_response=None):
        request = create_contentions.Request(claim_id=self.job.pending_claim_id, create_contentions=new_contentions)
        self.make_request(
            request=request,
            hoppy_client=HOPPY.get_client(ClientName.CREATE_CLAIM_CONTENTIONS),
            response_type=create_contentions.Response,
            expected_statuses=201,
        )
        self.send(event=event, ep400_contentions_response=ep400_contentions_response)

    @running_cancel_ep400_claim.enter
    def on_cancel_ep400_claim(self, event):
        request = cancel_claim.Request(
            claim_id=self.job.ep400_claim_id, lifecycle_status_reason_code=CANCEL_TRACKING_EP, close_reason_text=self.cancellation_reason
        )
        self.make_request(request=request, hoppy_client=HOPPY.get_client(ClientName.CANCEL_CLAIM), response_type=cancel_claim.Response)
        self.send(event=event)

    @running_add_claim_note_to_ep400.enter
    def on_add_claim_note_to_ep400(self, event):
        request = add_claim_note.Request(vbms_claim_id=self.job.ep400_claim_id, claim_notes=[self.cancellation_reason])
        self.make_request(request=request, hoppy_client=HOPPY.get_client(ClientName.BGS_ADD_CLAIM_NOTE), response_type=add_claim_note.Response)
        self.send(event=event)

    @running_get_pending_claim_failed_remove_special_issue.enter
    @running_get_pending_contentions_failed_remove_special_issue.enter
    @running_set_temp_station_of_jurisdiction_failed_remove_special_issue.enter
    @running_move_contentions_failed_remove_special_issue.enter
    def on_pre_cancel_step_failed_remove_special_issue_code(self, event, ep400_contentions_response=None):
        if ep400_contentions_response is None:
            request = get_contentions.Request(claim_id=self.job.ep400_claim_id)
            ep400_contentions_response = self.make_request(
                request=request, hoppy_client=HOPPY.get_client(ClientName.GET_CLAIM_CONTENTIONS), response_type=get_contentions.Response
            )

        contentions = (
            ep400_contentions_response.contentions
            if ep400_contentions_response is not None and ep400_contentions_response.status_code == 200 and ep400_contentions_response.contentions
            else []
        )

        if contentions:
            updates = []
            for contention in ContentionsUtil.to_existing_contentions(contentions):
                contention.special_issue_codes = (
                    [code for code in contention.special_issue_codes if code != EP_MERGE_SPECIAL_ISSUE_CODE] if contention.special_issue_codes else None
                )
                updates.append(contention)

            request = update_contentions.Request(claim_id=self.job.ep400_claim_id, update_contentions=updates)
            self.make_request(request=request, hoppy_client=HOPPY.get_client(ClientName.UPDATE_CLAIM_CONTENTIONS), response_type=update_contentions.Response)

        self.send(event=event)

    @running_move_contentions_failed_revert_temp_station_of_jurisdiction.enter
    @running_cancel_claim_failed_revert_temp_station_of_jurisdiction.enter
    def on_move_contentions_or_cancel_claim_failed_revert_temp_station_of_jurisdiction(self, event):
        request = tsoj.Request(temp_station_of_jurisdiction=self.original_tsoj, claim_id=self.job.ep400_claim_id)
        self.make_request(request=request, hoppy_client=HOPPY.get_client(ClientName.PUT_TSOJ), response_type=tsoj.Response)
        self.send(event=event)

    @completed_success.enter
    @completed_error.enter
    def on_completed(self, event):
        job_duration = (self.job.updated_at - self.job.created_at).total_seconds()
        self.log_metrics(job_duration)

        if self.job.state == JobState.COMPLETED_ERROR:
            logging.error(
                f"event=jobCompletedWithError "
                f"trigger={event} "
                f"job_id={self.job.job_id} "
                f"pending_claim_id={self.job.pending_claim_id} "
                f"ep400_claim_id={self.job.ep400_claim_id} "
                f"state={self.job.state} "
                f"errorState={self.job.error_state} "
                f"error=\"{jsonable_encoder(self.job.messages)}\" "
                f"job_duration_seconds={job_duration}"
            )
        else:
            logging.info(
                f"event=jobCompleted "
                f"trigger={event} "
                f"job_id={self.job.job_id} "
                f"pending_claim_id={self.job.pending_claim_id} "
                f"ep400_claim_id={self.job.ep400_claim_id} "
                f"state={self.job.state} "
                f"job_duration_seconds={job_duration}"
            )

    def log_metrics(self, job_duration):
        distribution(JOB_DURATION_METRIC, job_duration)

        if self.job.state == JobState.COMPLETED_SUCCESS:
            increment(JOB_SUCCESS_METRIC)
            distribution(JOB_NEW_CONTENTIONS_METRIC, self.num_new_contentions)
            if self.skipped_merge:
                increment(JOB_SKIPPED_MERGE_METRIC)
        else:
            increment(JOB_FAILURE_METRIC)
            increment(f'{JOB_ERROR_METRIC_PREFIX}.{self.job.error_state}')
            if self.job.error_state in ERROR_STATES_TO_LOG_METRICS:
                distribution(JOB_NEW_CONTENTIONS_METRIC, self.num_new_contentions)
                if self.skipped_merge:
                    increment(JOB_SKIPPED_MERGE_METRIC)

    def make_request(
        self, request: GeneralRequest, hoppy_client: AsyncHoppyClient, response_type: Type[GeneralResponse], expected_statuses: list[int] | int = 200
    ):
        if not isinstance(expected_statuses, list):
            expected_statuses = [expected_statuses]
        try:
            loop = asyncio.new_event_loop()
            req = hoppy_client.make_request(self.job.job_id, request.model_dump(by_alias=True))
            response = loop.run_until_complete(req)
            model = response_type.model_validate(response)
            if model.status_code not in expected_statuses:
                self.add_error(
                    model.messages
                    if model.messages
                    else f"client={hoppy_client.name} error='Unknown Downstream Error' status={model.status_code} status_message={model.status_message}"
                )
            return model
        except ValidationError as e:
            self.add_error(f"client={hoppy_client.name} error={e.errors(include_url=False, include_input=False)}")
        except ResponseException as e:
            self.add_error(f"client={hoppy_client.name} error={e.message}")
        except Exception as e:
            self.add_error(f"client={hoppy_client.name} error='Unknown Exception Caught {e}'")
        return None

    def has_error(self):
        return self.job.state == JobState.COMPLETED_ERROR

    def has_new_contentions(self, pending_contentions_response: get_contentions.Response, ep400_contentions_response: get_contentions.Response):
        contentions = ContentionsUtil.new_contentions(pending_contentions_response.contentions, ep400_contentions_response.contentions)
        self.num_new_contentions = len(contentions)
        return contentions

    def add_error(self, error):
        self.job.error(error)

    def add_message(self, message):
        self.job.add_message(message)
