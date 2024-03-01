import pytest
from httpx import AsyncClient
from integration.mq_endpoint import MqEndpoint
from src.python_src.api import app
from src.python_src.schema.merge_job import JobState

RESPONSE_DIR = './tests/responses'
response_200 = f'{RESPONSE_DIR}/200_response.json'
response_201 = f'{RESPONSE_DIR}/201_response.json'
response_204 = f'{RESPONSE_DIR}/204_response.json'
response_404 = f'{RESPONSE_DIR}/404_response.json'
response_400 = f'{RESPONSE_DIR}/400_response.json'
response_500 = f'{RESPONSE_DIR}/500_response.json'
pending_claim_200 = f'{RESPONSE_DIR}/get_pending_claim_200.json'
pending_contentions_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tendinitis_200.json'
ep400_contentions_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tinnitus_200.json'
ep400_duplicate_contentions_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tendinitis_200.json'

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


async def submit_request_and_process(client):
    request = {"pending_claim_id": pending_claim_id, "ep400_claim_id": ep400_claim_id}
    response = await client.post(url="/merge", json=request)

    response_json = assert_response(response, JobState.PENDING.value, status_code=202)
    job_id = response_json['job']['job_id']

    response = await client.get(url=f"/merge/{job_id}")

    return response


class TestMergeRequestBase:
    pass


class TestSuccess(TestMergeRequestBase):

    @pytest.mark.asyncio(scope="session")
    async def test_completed_success(
        self,
        get_claim_endpoint: MqEndpoint,
        get_claim_contentions_endpoint: MqEndpoint,
        put_tsoj_endpoint: MqEndpoint,
        create_claim_contentions_endpoint: MqEndpoint,
        cancel_claim_endpoint: MqEndpoint,
        add_claim_note_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_200])
        create_claim_contentions_endpoint.set_responses([response_201])
        cancel_claim_endpoint.set_responses([response_200])
        add_claim_note_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)

            assert_successful_response(response)

    @pytest.mark.asyncio(scope="session")
    async def test_completed_success_with_duplicate_contention(
        self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, put_tsoj_endpoint: MqEndpoint, cancel_claim_endpoint: MqEndpoint
    ):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_duplicate_contentions_200])
        put_tsoj_endpoint.set_responses([response_200])
        cancel_claim_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_successful_response(response)

    @pytest.mark.asyncio(scope="session")
    async def test_completed_no_ep400_contentions_on_first_attempt(
        self,
        get_claim_endpoint: MqEndpoint,
        get_claim_contentions_endpoint: MqEndpoint,
        put_tsoj_endpoint: MqEndpoint,
        create_claim_contentions_endpoint: MqEndpoint,
        cancel_claim_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, response_204, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_200])
        create_claim_contentions_endpoint.set_responses([response_201])
        cancel_claim_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_successful_response(response)


class TestErrorAtGetPendingClaim(TestMergeRequestBase):

    @pytest.mark.asyncio(scope="session")
    async def test(self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint):
        get_claim_endpoint.set_responses([response_500])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([ep400_contentions_200])
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_PENDING_CLAIM)

    @pytest.mark.asyncio(scope="session")
    async def test_error_at_remove_special_issue_fail_to_get_ep400_contentions(
        self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint
    ):
        get_claim_endpoint.set_responses([response_500])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE)

    @pytest.mark.asyncio(scope="session")
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint
    ):
        get_claim_endpoint.set_responses([response_500])

        # Needed after get claim failure
        get_claim_contentions_endpoint.set_responses([ep400_contentions_200])
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE)


class TestErrorAtGetPendingClaimContentions(TestMergeRequestBase):

    @pytest.mark.asyncio(scope="session")
    async def test(self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([response_500, ep400_contentions_200])

        # Needed after get claim failure, note second response from get_claim_contentions_endpoint above
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_PENDING_CLAIM_CONTENTIONS)

    @pytest.mark.asyncio(scope="session")
    async def test_error_at_remove_special_issue_fail_to_get_ep400_contentions(
        self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint
    ):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([response_500, response_500])
        # Note second response from get_claim_contentions_endpoint above

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_PENDING_CLAIM_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE)

    @pytest.mark.asyncio(scope="session")
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint, update_claim_contentions_endpoint: MqEndpoint
    ):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([response_500, ep400_contentions_200])

        # Needed after get claim failure, note second response from get_claim_contentions_endpoint above
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_PENDING_CLAIM_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE)


class TestErrorAtGetEp400ClaimContentions(TestMergeRequestBase):

    @pytest.mark.asyncio(scope="session")
    async def test(self, get_claim_endpoint: MqEndpoint, get_claim_contentions_endpoint: MqEndpoint):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, response_500])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.GET_EP400_CLAIM_CONTENTIONS)


class TestErrorAtSetTemporaryStationOfJurisdiction(TestMergeRequestBase):

    @pytest.mark.asyncio(scope="session")
    async def test(
        self,
        get_claim_endpoint: MqEndpoint,
        get_claim_contentions_endpoint: MqEndpoint,
        put_tsoj_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_500])
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.SET_TEMP_STATION_OF_JURISDICTION)

    @pytest.mark.asyncio(scope="session")
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self,
        get_claim_endpoint: MqEndpoint,
        get_claim_contentions_endpoint: MqEndpoint,
        put_tsoj_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_500])
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.SET_TEMP_STATION_OF_JURISDICTION_FAILED_REMOVE_SPECIAL_ISSUE)


class TestErrorAtMoveContentionsToPendingClaim(TestMergeRequestBase):

    @pytest.mark.asyncio(scope="session")
    async def test(
        self,
        get_claim_endpoint: MqEndpoint,
        get_claim_contentions_endpoint: MqEndpoint,
        put_tsoj_endpoint: MqEndpoint,
        create_claim_contentions_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_200, response_200])  # Second response is to revert the tsoj
        create_claim_contentions_endpoint.set_responses([response_500])
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.MOVE_CONTENTIONS_TO_PENDING_CLAIM)

    @pytest.mark.asyncio(scope="session")
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self,
        get_claim_endpoint: MqEndpoint,
        get_claim_contentions_endpoint: MqEndpoint,
        put_tsoj_endpoint: MqEndpoint,
        create_claim_contentions_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_200, response_200])
        create_claim_contentions_endpoint.set_responses([response_500])
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.MOVE_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE)

    @pytest.mark.asyncio(scope="session")
    async def test_error_at_revert_tsoj(
        self,
        get_claim_endpoint: MqEndpoint,
        get_claim_contentions_endpoint: MqEndpoint,
        put_tsoj_endpoint: MqEndpoint,
        create_claim_contentions_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_200, response_500])  # Note the 500 on second response
        create_claim_contentions_endpoint.set_responses([response_500])
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.MOVE_CONTENTIONS_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION)


class TestErrorAtCancelClaim(TestMergeRequestBase):

    @pytest.mark.asyncio(scope="session")
    async def test(
        self,
        get_claim_endpoint: MqEndpoint,
        get_claim_contentions_endpoint: MqEndpoint,
        put_tsoj_endpoint: MqEndpoint,
        create_claim_contentions_endpoint: MqEndpoint,
        cancel_claim_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_200])
        create_claim_contentions_endpoint.set_responses([response_201])
        cancel_claim_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.CANCEL_EP400_CLAIM)

    @pytest.mark.asyncio(scope="session")
    async def test_error_at_revert_tsoj(
        self,
        get_claim_endpoint: MqEndpoint,
        get_claim_contentions_endpoint: MqEndpoint,
        put_tsoj_endpoint: MqEndpoint,
        create_claim_contentions_endpoint: MqEndpoint,
        cancel_claim_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_200, response_500])  # Note the 500 on second response
        create_claim_contentions_endpoint.set_responses([response_201])
        cancel_claim_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.CANCEL_CLAIM_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION)


class TestErrorAtAddClaimNote(TestMergeRequestBase):

    @pytest.mark.asyncio(scope="session")
    async def test(
        self,
        get_claim_endpoint: MqEndpoint,
        get_claim_contentions_endpoint: MqEndpoint,
        put_tsoj_endpoint: MqEndpoint,
        create_claim_contentions_endpoint: MqEndpoint,
        cancel_claim_endpoint: MqEndpoint,
        add_claim_note_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_200])
        create_claim_contentions_endpoint.set_responses([response_201])
        cancel_claim_endpoint.set_responses([response_200])
        add_claim_note_endpoint.set_responses([response_500])
        update_claim_contentions_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.ADD_CLAIM_NOTE_TO_EP400)

    @pytest.mark.asyncio(scope="session")
    async def test_error_at_remove_special_issue_fail_to_update_ep400_contentions(
        self,
        get_claim_endpoint: MqEndpoint,
        get_claim_contentions_endpoint: MqEndpoint,
        put_tsoj_endpoint: MqEndpoint,
        create_claim_contentions_endpoint: MqEndpoint,
        cancel_claim_endpoint: MqEndpoint,
        add_claim_note_endpoint: MqEndpoint,
        update_claim_contentions_endpoint: MqEndpoint,
    ):
        get_claim_endpoint.set_responses([pending_claim_200])
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_200])
        create_claim_contentions_endpoint.set_responses([response_201])
        cancel_claim_endpoint.set_responses([response_200])
        add_claim_note_endpoint.set_responses([response_500])
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client)
            assert_error_response(response, JobState.ADD_CLAIM_NOTE_TO_EP400)
