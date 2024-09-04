import pytest
from conftest import (
    ep400_claim_200,
    ep400_claim_200_closed,
    ep400_claim_200_missing_claim_type_code,
    ep400_claim_200_missing_ep_code,
    ep400_claim_200_unsupported_claim_type_code,
    ep400_claim_200_unsupported_ep_code,
    ep400_claim_contentions_deactive_special_issue_code_200,
    ep400_contentions_200,
    ep400_duplicate_contentions_200,
    pending_claim_200,
    pending_claim_200_closed,
    pending_contentions_200,
    response_200,
    response_204,
    response_500,
)
from httpx import AsyncClient

from integration.mq_endpoint import MqEndpoint
from src.python_src.api import app
from src.python_src.schema.merge_job import JobState

pending_claim_id = 1
ep400_claim_id = 2


def assert_response(response, expected_state: JobState, status_code: int = 200):
    assert response.status_code == status_code
    response_json = response.json()
    assert response_json is not None
    assert response_json['job']['pending_claim_id'] == pending_claim_id
    assert response_json['job']['ep400_claim_id'] == ep400_claim_id
    assert response_json['job']['state'] == expected_state
    return response_json


def assert_successful_response(response_json):
    return assert_response(response_json, JobState.COMPLETED_SUCCESS)


def assert_error_response(response_json, expected_error_state):
    response_json = assert_response(response_json, JobState.COMPLETED_ERROR)
    assert response_json['job']['error_state'] == expected_error_state
    return response_json


def assert_abort_response(response_json, expected_error_state):
    response_json = assert_response(response_json, JobState.ABORTED)
    assert response_json['job']['error_state'] == expected_error_state
    return response_json


async def submit_request_and_process(client):
    request = {'pending_claim_id': pending_claim_id, 'ep400_claim_id': ep400_claim_id}
    response = await client.post(url='/merge', json=request)

    response_json = assert_response(response, JobState.PENDING.value, status_code=202)
    job_id = response_json['job']['job_id']

    response = await client.get(url=f'/merge/{job_id}')

    return response


class TestMergeRequestBase:
    pass


class TestSuccess(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test_completed_success(self):
        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_successful_response(response)

    @pytest.mark.asyncio(scope='session')
    async def test_completed_success_with_duplicate_contention(self, get_claim_contentions_endpoint: MqEndpoint):
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_duplicate_contentions_200])
        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_successful_response(response)

    @pytest.mark.asyncio(scope='session')
    async def test_completed_no_ep400_contentions_on_first_attempt(self, get_claim_contentions_endpoint: MqEndpoint):
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, response_204, ep400_contentions_200])
        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_successful_response(response)


class TestErrorAtGetPendingClaim(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test_500(self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint):
        get_claim_endpoint.set_responses([response_500])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([ep400_contentions_200])
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_PENDING_CLAIM)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_get_ep400_contentions(
        self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint
    ):
        get_claim_endpoint.set_responses([response_500])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint
    ):
        get_claim_endpoint.set_responses([response_500])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([ep400_contentions_200])
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE)


class TestAbortAtGetPendingClaim(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test_claim_not_open(
        self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint
    ):
        get_claim_endpoint.set_responses([pending_claim_200_closed])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([ep400_contentions_200])
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_abort_response(response, JobState.GET_PENDING_CLAIM)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_get_ep400_contentions(
        self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint
    ):
        get_claim_endpoint.set_responses([pending_claim_200_closed])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_abort_response(response, JobState.ABORTING)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint
    ):
        get_claim_endpoint.set_responses([pending_claim_200_closed])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([ep400_contentions_200])
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_abort_response(response, JobState.ABORTING)


class TestErrorAtGetEP400Claim(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test(self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint):
        get_claim_endpoint.set_responses([pending_claim_200, response_500])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([ep400_contentions_200])
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_EP400_CLAIM)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_get_ep400_contentions(
        self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint
    ):
        get_claim_endpoint.set_responses([pending_claim_200, response_500])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_EP400_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint
    ):
        get_claim_endpoint.set_responses([pending_claim_200, response_500])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([ep400_contentions_200])
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_EP400_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE)


@pytest.mark.parametrize(
    'response',
    [
        pytest.param(ep400_claim_200_closed, id='claim is closed'),
        pytest.param(ep400_claim_200_missing_ep_code, id='missing ep code'),
        pytest.param(ep400_claim_200_unsupported_ep_code, id='unsupported ep code'),
        pytest.param(ep400_claim_200_missing_claim_type_code, id='missing claim type code'),
        pytest.param(ep400_claim_200_unsupported_claim_type_code, id='unsupported claim type code'),
    ],
)
class TestAbortAtGetEP400Claim(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test(self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint, response):
        get_claim_endpoint.set_responses([pending_claim_200, response])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([ep400_contentions_200])
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_abort_response(response, JobState.GET_EP400_CLAIM)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_get_ep400_contentions(
        self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint, response
    ):
        get_claim_endpoint.set_responses([pending_claim_200, response])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_abort_response(response, JobState.ABORTING)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint, response
    ):
        get_claim_endpoint.set_responses([pending_claim_200, response])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([ep400_contentions_200])
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_abort_response(response, JobState.ABORTING)


class TestErrorAtGetPendingClaimContentions(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test(self, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint):
        get_claim_contentions_endpoint.set_responses([response_500, ep400_contentions_200])

        # Needed after get claim failure, note second response from get_claim_contentions_endpoint above
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_PENDING_CLAIM_CONTENTIONS)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_get_ep400_contentions(
        self, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint
    ):
        get_claim_contentions_endpoint.set_responses([response_500, response_500])
        # Note second response from get_claim_contentions_endpoint above

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_PENDING_CLAIM_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint
    ):
        get_claim_contentions_endpoint.set_responses([response_500, ep400_contentions_200])

        # Needed after get claim failure, note second response from get_claim_contentions_endpoint above
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_PENDING_CLAIM_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE)


class TestErrorAtGetEp400ClaimContentions(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test(self, get_claim_contentions_endpoint: MqEndpoint):
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_EP400_CLAIM_CONTENTIONS)


class TestAbortAtGetEp400ClaimContentions(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test_no_contentions_found(self, get_claim_contentions_endpoint: MqEndpoint):
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, response_204, response_204])
        # Note the second 204 is because the tests are set up to try to get the ep400 contentions twice in pyproject.toml

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_abort_response(response, JobState.GET_EP400_CLAIM_CONTENTIONS)

    @pytest.mark.asyncio(scope='session')
    async def test_found_contention_with_deactive_special_issue_code(self, get_claim_contentions_endpoint: MqEndpoint):
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_claim_contentions_deactive_special_issue_code_200])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_abort_response(response, JobState.GET_EP400_CLAIM_CONTENTIONS)


class TestErrorAtCheckPendingIsOpen(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test(
        self,
        get_claim_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200, ep400_claim_200, response_500])

        # Needed after failure
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.CHECK_PENDING_EP_IS_OPEN)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self,
        get_claim_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200, ep400_claim_200, response_500])

        # Needed after failure
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.CHECK_PENDING_EP_IS_OPEN_FAILED_REMOVE_SPECIAL_ISSUE)


class TestAbortAtCheckPendingIsOpen(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test(
        self,
        get_claim_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200, ep400_claim_200, pending_claim_200_closed])

        # Needed after abort
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_abort_response(response, JobState.CHECK_PENDING_EP_IS_OPEN)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self,
        get_claim_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200, ep400_claim_200, pending_claim_200_closed])

        # Needed after abort
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_abort_response(response, JobState.ABORTING)


class TestErrorAtCheckEp400IsOpen(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test(
        self,
        get_claim_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200, ep400_claim_200, pending_claim_200, response_500])

        # Needed after abort
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.CHECK_EP400_IS_OPEN)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self,
        get_claim_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200, ep400_claim_200, pending_claim_200, response_500])

        # Needed after abort
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.CHECK_EP400_IS_OPEN_FAILED_REMOVE_SPECIAL_ISSUE)


class TestAbortAtCheckEp400IsOpen(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test(
        self,
        get_claim_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200, ep400_claim_200, pending_claim_200, ep400_claim_200_closed])

        # Needed after abort
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_abort_response(response, JobState.CHECK_EP400_IS_OPEN)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self,
        get_claim_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200, ep400_claim_200, pending_claim_200, ep400_claim_200_closed])

        # Needed after abort
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_abort_response(response, JobState.ABORTING)


class TestErrorAtSetTemporaryStationOfJurisdiction(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test(
        self,
        put_tsoj_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        put_tsoj_endpoint.set_responses([response_500])

        # Needed after failure
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.SET_TEMP_STATION_OF_JURISDICTION)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self,
        put_tsoj_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        put_tsoj_endpoint.set_responses([response_500])

        # Needed after failure
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.SET_TEMP_STATION_OF_JURISDICTION_FAILED_REMOVE_SPECIAL_ISSUE)


class TestErrorAtMoveContentionsToPendingClaim(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test(
        self,
        put_tsoj_endpoint: MqEndpoint,
        create_claim_contentions_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        put_tsoj_endpoint.set_responses([response_200, response_200])  # Second response is to revert the tsoj
        create_claim_contentions_endpoint.set_responses([response_500])

        # Needed after failure
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.MOVE_CONTENTIONS_TO_PENDING_CLAIM)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self,
        put_tsoj_endpoint: MqEndpoint,
        create_claim_contentions_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        put_tsoj_endpoint.set_responses([response_200, response_200])  # Second response is to revert the tsoj
        create_claim_contentions_endpoint.set_responses([response_500])

        # Needed after failure
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.MOVE_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_revert_tsoj(
        self,
        put_tsoj_endpoint: MqEndpoint,
        create_claim_contentions_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        put_tsoj_endpoint.set_responses([response_200, response_500])  # Note the 500 on second response
        create_claim_contentions_endpoint.set_responses([response_500])

        # Needed after failure
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.MOVE_CONTENTIONS_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION)


class TestErrorAtCancelClaim(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test(
        self,
        put_tsoj_endpoint: MqEndpoint,
        cancel_claim_endpoint: MqEndpoint,
    ):
        put_tsoj_endpoint.set_responses([response_200, response_200])  # Note the 200 to revert tsoj

        # Needed after failure
        cancel_claim_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.CANCEL_EP400_CLAIM)

    @pytest.mark.asyncio(scope='session')
    async def test_error_at_revert_tsoj(
        self,
        put_tsoj_endpoint: MqEndpoint,
        cancel_claim_endpoint: MqEndpoint,
    ):
        put_tsoj_endpoint.set_responses([response_200, response_500])  # Note the 500 on second response

        # Needed after failure
        cancel_claim_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.CANCEL_CLAIM_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION)


class TestErrorAtAddClaimNote(TestMergeRequestBase):
    @pytest.mark.asyncio(scope='session')
    async def test(
        self,
        add_claim_note_endpoint: MqEndpoint,
    ):
        add_claim_note_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url='http://test') as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.ADD_CLAIM_NOTE_TO_EP400)
