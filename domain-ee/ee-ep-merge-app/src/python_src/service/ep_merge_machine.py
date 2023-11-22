import asyncio
import logging
from typing import Type

from hoppy.exception import ResponseException
from model import cancel_claim, create_contentions, get_claim, get_contentions
from model import update_temp_station_of_jurisdiction as tsoj
from model.merge_job import JobState, MergeJob
from model.request import GeneralRequest
from model.response import GeneralResponse
from pydantic import ValidationError
from service.hoppy_service import HOPPY, ClientName
from statemachine import State, StateMachine
from util.contentions_util import CompareException, ContentionsUtil, MergeException

CANCEL_TRACKING_EP = "60"
CANCELLATION_REASON_FORMAT = "Issues moved into or confirmed in pending EP{ep_code} - claim #{claim_id}"


class EpMergeMachine(StateMachine):
    job: MergeJob | None = None
    cancellation_reason: str | None = None

    # States:
    pending = State(initial=True, value=JobState.PENDING)
    running_get_pending_claim = State(value=JobState.RUNNING_GET_PENDING_CLAIM)
    running_get_pending_contentions = State(value=JobState.RUNNING_GET_PENDING_CLAIM_CONTENTIONS)
    running_get_ep400_contentions = State(value=JobState.RUNNING_GET_EP400_CLAIM_CONTENTIONS)
    running_set_temp_station_of_jurisdiction = State(value=JobState.RUNNING_SET_TEMP_STATION_OF_JURISDICTION)
    running_merge_contentions = State(value=JobState.RUNNING_MERGE_CONTENTIONS)
    running_move_contentions_to_pending_claim = State(value=JobState.RUNNING_MOVE_CONTENTIONS_TO_PENDING_CLAIM)
    running_cancel_ep400_claim = State(value=JobState.RUNNING_CANCEL_EP400_CLAIM)
    completed_success = State(final=True, value=JobState.COMPLETED_SUCCESS)
    completed_error = State(final=True, value=JobState.COMPLETED_ERROR)

    process = (
            pending.to(running_get_pending_claim)
            | running_get_pending_claim.to(running_get_pending_contentions, unless="has_error")
            | running_get_pending_claim.to(completed_error, cond="has_error")
            | running_get_pending_contentions.to(running_get_ep400_contentions, unless="has_error")
            | running_get_pending_contentions.to(completed_error, cond="has_error")
            | running_get_ep400_contentions.to(running_set_temp_station_of_jurisdiction, unless="has_error")
            | running_get_ep400_contentions.to(completed_error, cond="has_error")
            | running_set_temp_station_of_jurisdiction.to(running_merge_contentions,
                                                          unless=["is_duplicate", "has_error"])
            | running_set_temp_station_of_jurisdiction.to(running_cancel_ep400_claim, cond="is_duplicate",
                                                          unless="has_error")
            | running_set_temp_station_of_jurisdiction.to(completed_error, cond="has_error")
            | running_merge_contentions.to(running_move_contentions_to_pending_claim, unless="has_error")
            | running_merge_contentions.to(completed_error, cond="has_error")
            | running_move_contentions_to_pending_claim.to(running_cancel_ep400_claim, unless="has_error")
            | running_move_contentions_to_pending_claim.to(completed_error, cond="has_error")
            | running_cancel_ep400_claim.to(completed_success, unless="has_error")
            | running_cancel_ep400_claim.to(completed_error, cond="has_error")
    )

    def __init__(self, merge_job: MergeJob):
        self.job = merge_job
        super().__init__()

    def on_transition(self, source, target):
        logging.info(f"event=jobTransition job_id={self.job.job_id} old={source.value} new={target.value}")
        self.job.state = target.value

    @pending.exit
    def on_start_process(self):
        logging.info(f"event=jobStarted job_id={self.job.job_id}")

    @running_get_pending_claim.enter
    def on_get_pending_claim(self):
        request = get_claim.Request(claim_id=self.job.pending_claim_id)
        response = self.make_request(
            request=request,
            hoppy_client=HOPPY.get_client(ClientName.GET_CLAIM),
            response_type=get_claim.Response)

        if response is not None and response.status_code == 200:
            if response.claim is None or response.claim.end_product_code is None:
                logging.info(self.job.state)
                self.log_error(f"Pending claim #{self.job.pending_claim_id} does not have an end product code")
            else:
                self.cancellation_reason = CANCELLATION_REASON_FORMAT.format(ep_code=response.claim.end_product_code,
                                                                             claim_id=self.job.pending_claim_id)
        self.process()

    @running_get_pending_contentions.enter
    def on_get_pending_contentions(self):
        request = get_contentions.Request(claim_id=self.job.pending_claim_id)
        response = self.make_request(
            request=request,
            hoppy_client=HOPPY.get_client(ClientName.GET_CLAIM_CONTENTIONS),
            response_type=get_contentions.Response)
        self.process(pending_contentions=response)

    @running_get_ep400_contentions.enter
    def on_get_ep400_contentions(self, pending_contentions=None):
        request = get_contentions.Request(claim_id=self.job.ep400_claim_id)
        response = self.make_request(
            request=request,
            hoppy_client=HOPPY.get_client(ClientName.GET_CLAIM_CONTENTIONS),
            response_type=get_contentions.Response)
        self.process(pending_contentions=pending_contentions, ep400_contentions=response)

    @running_set_temp_station_of_jurisdiction.enter
    def on_set_temp_station_of_jurisdiction(self, pending_contentions=None, ep400_contentions=None):
        request = tsoj.Request(temp_station_of_jurisdiction="398", claim_id=self.job.ep400_claim_id)
        self.make_request(
            request=request,
            hoppy_client=(HOPPY.get_client(ClientName.PUT_TSOJ)),
            response_type=tsoj.Response)
        self.process(pending_contentions=pending_contentions, ep400_contentions=ep400_contentions)

    @running_merge_contentions.enter
    def on_merge_contentions(self, pending_contentions=None, ep400_contentions=None):
        merged_contentions = None
        try:
            merged_contentions = ContentionsUtil.merge_claims(pending_contentions, ep400_contentions)
        except (MergeException, CompareException) as e:
            self.log_error(e.message)
        self.process(merged_contentions=merged_contentions)

    @running_move_contentions_to_pending_claim.enter
    def on_move_contentions_to_pending_claim(self, merged_contentions=None):
        request = create_contentions.Request(claim_id=self.job.pending_claim_id, create_contentions=merged_contentions)
        self.make_request(
            request=request,
            hoppy_client=HOPPY.get_client(ClientName.CREATE_CLAIM_CONTENTIONS),
            response_type=create_contentions.Response,
            expected_status=201)
        self.process()

    @running_cancel_ep400_claim.enter
    def on_cancel_ep400_claim(self):
        request = cancel_claim.Request(claim_id=self.job.ep400_claim_id,
                                       lifecycle_status_reason_code=CANCEL_TRACKING_EP,
                                       close_reason_text=self.cancellation_reason)
        self.make_request(
            request=request,
            hoppy_client=HOPPY.get_client(ClientName.CANCEL_CLAIM),
            response_type=cancel_claim.Response)
        self.process()

    @completed_success.enter
    @completed_error.enter
    def on_completed(self, state):
        logging.info(f"event=jobCompleted job_id={self.job.job_id} state={state.value}")

    def make_request(self,
                     request: GeneralRequest,
                     hoppy_client,
                     response_type: Type[GeneralResponse],
                     expected_status: int = 200):
        try:
            loop = asyncio.new_event_loop()
            req = hoppy_client.make_request(self.job.job_id, request.model_dump(by_alias=True))
            response = loop.run_until_complete(req)
            model = response_type.model_validate(response)
            if model.status_code != expected_status:
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

    def is_duplicate(self, pending_contentions: get_contentions.Response, ep400_contentions: get_contentions.Response):
        try:
            return not ContentionsUtil.new_contentions(pending_contentions.contentions, ep400_contentions.contentions)
        except CompareException as e:
            self.log_error(e.message)
        return None

    def log_error(self, error):
        logging.error(f"event=errorProcessingJob "
                      f"job_id={self.job.job_id} "
                      f"state={self.job.state} "
                      f"error=\'{error}\'")
        self.job.error(self.job.state, error)
