import asyncio
import logging
from typing import Type

from hoppy.exception import ResponseException
from model import cancel_claim, get_contentions, update_contentions
from model import update_temp_station_of_jurisdiction as tsoj
from model.merge_job import JobState, MergeJob
from model.request import GeneralRequest
from model.response import GeneralResponse
from pydantic import ValidationError
from service.hoppy_service import ClientName, HoppyService
from statemachine import State, StateMachine
from util.contentions_util import ContentionsUtil, MergeException

CANCELLATION_REASON = "Issues moved to pending EP Claim ID #%d"


class EpMergeMachine(StateMachine):
    job: MergeJob | None = None

    # States:
    pending = State(initial=True, value=JobState.PENDING)
    running_get_pending_contentions = State(value=JobState.RUNNING_GET_PENDING_CLAIM_CONTENTIONS)
    running_get_ep400_contentions = State(value=JobState.RUNNING_GET_EP400_CLAIM_CONTENTIONS)
    running_set_temp_station_of_jurisdiction = State(value=JobState.RUNNING_SET_TEMP_STATION_OF_JURISDICTION)
    running_merge_contentions = State(value=JobState.RUNNING_MERGE_CONTENTIONS)
    running_update_pending_claim_contentions = State(value=JobState.RUNNING_UPDATE_PENDING_CLAIM_CONTENTIONS)
    running_cancel_ep400_claim = State(value=JobState.RUNNING_CANCEL_EP400_CLAIM)
    completed_success = State(final=True, value=JobState.COMPLETED_SUCCESS)
    completed_error = State(final=True, value=JobState.COMPLETED_ERROR)

    process = (
            pending.to(running_get_pending_contentions)
            | running_get_pending_contentions.to(running_get_ep400_contentions, unless="has_error")
            | running_get_pending_contentions.to(completed_error, cond="has_error")
            | running_get_ep400_contentions.to(running_set_temp_station_of_jurisdiction, unless="has_error")
            | running_get_ep400_contentions.to(completed_error, cond="has_error")
            | running_set_temp_station_of_jurisdiction.to(running_merge_contentions, unless="has_error")
            | running_set_temp_station_of_jurisdiction.to(completed_error, cond="has_error")
            | running_merge_contentions.to(running_update_pending_claim_contentions, unless="has_error")
            | running_merge_contentions.to(completed_error, cond="has_error")
            | running_update_pending_claim_contentions.to(running_cancel_ep400_claim, unless="has_error")
            | running_update_pending_claim_contentions.to(completed_error, cond="has_error")
            | running_cancel_ep400_claim.to(completed_success, unless="has_error")
            | running_cancel_ep400_claim.to(completed_error, cond="has_error")
    )

    def __init__(self, hoppy_service: HoppyService, merge_job: MergeJob):
        self.hoppy_service = hoppy_service
        self.job = merge_job
        super().__init__()

    def on_transition(self, source, target):
        logging.info(f"event=jobTransition job_id={self.job.job_id} old={source.value} new={target.value}")
        self.job.state = target.value

    @pending.exit
    def on_start_process(self):
        logging.info(f"event=jobStarted job_id={self.job.job_id}")

    @running_get_pending_contentions.enter
    def on_get_pending_contentions(self):
        request = get_contentions.Request(claim_id=self.job.pending_claim_id)
        response = self.make_request(
            request=request,
            hoppy_client=self.hoppy_service.get_client(ClientName.GET_CLAIM_CONTENTIONS),
            response_type=get_contentions.Response)
        self.process(pending_contentions=response)

    @running_get_ep400_contentions.enter
    def on_get_ep400_contentions(self, pending_contentions=None):
        request = get_contentions.Request(claim_id=self.job.ep400_claim_id)
        response = self.make_request(
            request=request,
            hoppy_client=self.hoppy_service.get_client(ClientName.GET_CLAIM_CONTENTIONS),
            response_type=get_contentions.Response)
        self.process(pending_contentions=pending_contentions, ep400_contentions=response)

    @running_set_temp_station_of_jurisdiction.enter
    def on_set_temp_station_of_jurisdiction(self, pending_contentions=None, ep400_contentions=None):
        request = tsoj.Request(temp_station_of_jurisdiction="398", claim_id=self.job.pending_claim_id)
        self.make_request(
            request=request,
            hoppy_client=(self.hoppy_service.get_client(ClientName.PUT_TSOJ)),
            response_type=tsoj.Response)
        self.process(pending_contentions=pending_contentions, ep400_contentions=ep400_contentions)

    @running_merge_contentions.enter
    def on_merge_contentions(self, pending_contentions=None, ep400_contentions=None):
        merged_contentions = None
        try:
            merged_contentions = ContentionsUtil.merge_claims(pending_contentions, ep400_contentions)
        except MergeException as e:
            self.log_error(e.message)
        self.process(merged_contentions=merged_contentions)

    @running_update_pending_claim_contentions.enter
    def on_update_pending_claim_contentions(self, merged_contentions=None):
        request = update_contentions.Request(claim_id=self.job.pending_claim_id, update_contentions=merged_contentions)
        self.make_request(
            request=request,
            hoppy_client=self.hoppy_service.get_client(ClientName.UPDATE_CLAIM_CONTENTIONS),
            response_type=update_contentions.Response)
        self.process()

    @running_cancel_ep400_claim.enter
    def on_cancel_ep400_claim(self):
        reason = CANCELLATION_REASON % self.job.pending_claim_id
        request = cancel_claim.Request(claim_id=self.job.ep400_claim_id,
                                       lifecycle_status_reason_code="65",
                                       close_reason_text=reason)
        self.make_request(
            request=request,
            hoppy_client=self.hoppy_service.get_client(ClientName.CANCEL_CLAIM),
            response_type=cancel_claim.Response)
        self.process()

    @completed_success.enter
    @completed_error.enter
    def on_completed(self, state):
        logging.info(f"event=jobCompleted job_id={self.job.job_id} state={state.value}")

    def make_request(self,
                     request: GeneralRequest,
                     hoppy_client,
                     response_type: Type[GeneralResponse]):
        try:
            loop = asyncio.new_event_loop()
            req = hoppy_client.make_request(self.job.job_id, request.model_dump(by_alias=True))
            response = loop.run_until_complete(req)
            model = response_type.model_validate(response)
            if model.status_code != 200:
                self.log_error(model.messages)
            return model
        except ValidationError as e:
            self.log_error(e.errors(include_url=False, include_input=False))
        except ResponseException as e:
            self.log_error(e.message)
        except Exception as e:
            self.log_error(f"Unknown Exception Caught {e}")
        return None

    def has_error(self):
        return self.job.state == JobState.COMPLETED_ERROR

    def log_error(self, error):
        logging.error(f"event=errorProcessingJob "
                      f"job_id={self.job.job_id} "
                      f"state={self.job.state} "
                      f"error=\'{error}\'")
        self.job.error(self.job.state, error)
