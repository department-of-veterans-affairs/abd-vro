import asyncio

import pytest
import pytest_asyncio
from httpx import AsyncClient
from integration.mq_endpoint import MqEndpoint
from model.merge_job import JobState
from src.python_src.api import app, on_shut_down, on_start_up

RESPONSE_DIR = './tests/responses'
response_200 = f'{RESPONSE_DIR}/200_response.json'
response_404 = f'{RESPONSE_DIR}/404_response.json'
response_400 = f'{RESPONSE_DIR}/400_response.json'
response_500 = f'{RESPONSE_DIR}/500_response.json'
pending_contentions_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tendinitis_200.json'
ep400_contentions_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tinitus_200.json'
ep400_duplicate_contentions_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tendinitis_200.json'


@pytest.fixture(scope="session")
def event_loop():
    try:
        loop = asyncio.get_running_loop()
    except RuntimeError:
        loop = asyncio.new_event_loop()
    yield loop
    loop.close()


@pytest_asyncio.fixture(autouse=True, scope="session")
async def app_lifespan():
    await on_start_up()
    yield
    await on_shut_down()


@pytest.fixture(autouse=True)
def reset_responses(get_claim_contentions_endpoint: MqEndpoint,
                    put_tsoj_endpoint: MqEndpoint,
                    update_claim_contentions_endpoint: MqEndpoint,
                    cancel_claim_endpoint: MqEndpoint):
    get_claim_contentions_endpoint.set_responses()
    put_tsoj_endpoint.set_responses()
    update_claim_contentions_endpoint.set_responses()
    cancel_claim_endpoint.set_responses()


class TestMergeRequest:
    pending_claim_id = 1
    ep400_claim_id = 2

    async def submit_request(self, client):
        request = {"pending_claim_id": self.pending_claim_id, "ep400_claim_id": self.ep400_claim_id}
        response = await client.post(url="/merge", json=request)
        assert response.status_code == 202

        response_json = response.json()
        assert response_json is not None
        assert response_json['job']['pending_claim_id'] == self.pending_claim_id
        assert response_json['job']['ep400_claim_id'] == self.ep400_claim_id
        assert response_json['job']['state'] == JobState.PENDING.value
        return response_json['job']['job_id']

    @pytest.mark.asyncio
    async def test_completed_success(self,
                                     get_claim_contentions_endpoint: MqEndpoint,
                                     put_tsoj_endpoint: MqEndpoint,
                                     update_claim_contentions_endpoint: MqEndpoint,
                                     cancel_claim_endpoint: MqEndpoint):
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_200])
        update_claim_contentions_endpoint.set_responses([response_200])
        cancel_claim_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url="http://test") as client:
            job_id = await self.submit_request(client)

            response = await client.get(url=f"/merge/{job_id}")
            assert response.status_code == 200

            response_json = response.json()
            assert response_json is not None
            assert response_json['job']['pending_claim_id'] == self.pending_claim_id
            assert response_json['job']['ep400_claim_id'] == self.ep400_claim_id
            assert response_json['job']['state'] == JobState.COMPLETED_SUCCESS.value

    @pytest.mark.asyncio
    async def test_completed_success_with_duplicate_contention(self,
                                                               get_claim_contentions_endpoint: MqEndpoint,
                                                               put_tsoj_endpoint: MqEndpoint,
                                                               cancel_claim_endpoint: MqEndpoint):
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_duplicate_contentions_200])
        put_tsoj_endpoint.set_responses([response_200])
        cancel_claim_endpoint.set_responses([response_200])

        async with AsyncClient(app=app, base_url="http://test") as client:
            job_id = await self.submit_request(client)

            response = await client.get(url=f"/merge/{job_id}")
            assert response.status_code == 200

            response_json = response.json()
            assert response_json is not None
            assert response_json['job']['pending_claim_id'] == self.pending_claim_id
            assert response_json['job']['ep400_claim_id'] == self.ep400_claim_id
            assert response_json['job']['state'] == JobState.COMPLETED_SUCCESS.value

    @pytest.mark.asyncio
    async def test_completed_error_at_get_pending_contentions(self,
                                                              get_claim_contentions_endpoint: MqEndpoint):
        get_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url="http://test") as client:
            job_id = await self.submit_request(client)

            response = await client.get(url=f"/merge/{job_id}")
            assert response.status_code == 200

            response_json = response.json()
            assert response_json is not None
            assert response_json['job']['pending_claim_id'] == self.pending_claim_id
            assert response_json['job']['ep400_claim_id'] == self.ep400_claim_id
            assert response_json['job']['state'] == JobState.COMPLETED_ERROR.value
            assert response_json['job']['error_state'] == JobState.RUNNING_GET_PENDING_CLAIM_CONTENTIONS.value

    @pytest.mark.asyncio
    async def test_completed_error_at_get_ep400_contentions(self, get_claim_contentions_endpoint: MqEndpoint):
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, response_500])

        async with AsyncClient(app=app, base_url="http://test") as client:
            job_id = await self.submit_request(client)

            response = await client.get(url=f"/merge/{job_id}")
            assert response.status_code == 200

            response_json = response.json()
            assert response_json is not None
            assert response_json['job']['pending_claim_id'] == self.pending_claim_id
            assert response_json['job']['ep400_claim_id'] == self.ep400_claim_id
            assert response_json['job']['state'] == JobState.COMPLETED_ERROR.value
            assert response_json['job']['error_state'] == JobState.RUNNING_GET_EP400_CLAIM_CONTENTIONS.value

    @pytest.mark.asyncio
    async def test_completed_error_at_set_tsoj(self,
                                               get_claim_contentions_endpoint: MqEndpoint,
                                               put_tsoj_endpoint: MqEndpoint):
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url="http://test") as client:
            job_id = await self.submit_request(client)

            response = await client.get(url=f"/merge/{job_id}")
            assert response.status_code == 200

            response_json = response.json()
            assert response_json is not None
            assert response_json['job']['pending_claim_id'] == self.pending_claim_id
            assert response_json['job']['ep400_claim_id'] == self.ep400_claim_id
            assert response_json['job']['state'] == JobState.COMPLETED_ERROR.value
            assert response_json['job']['error_state'] == JobState.RUNNING_SET_TEMP_STATION_OF_JURISDICTION.value

    @pytest.mark.asyncio
    async def test_completed_error_at_update_claim_contentions(self,
                                                               get_claim_contentions_endpoint: MqEndpoint,
                                                               put_tsoj_endpoint: MqEndpoint,
                                                               update_claim_contentions_endpoint: MqEndpoint):
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_200])
        update_claim_contentions_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url="http://test") as client:
            job_id = await self.submit_request(client)

            response = await client.get(url=f"/merge/{job_id}")
            assert response.status_code == 200

            response_json = response.json()
            assert response_json is not None
            assert response_json['job']['pending_claim_id'] == self.pending_claim_id
            assert response_json['job']['ep400_claim_id'] == self.ep400_claim_id
            assert response_json['job']['state'] == JobState.COMPLETED_ERROR.value
            assert response_json['job']['error_state'] == JobState.RUNNING_UPDATE_PENDING_CLAIM_CONTENTIONS.value

    @pytest.mark.asyncio
    async def test_completed_error_at_cancel_claim(self,
                                                   get_claim_contentions_endpoint: MqEndpoint,
                                                   put_tsoj_endpoint: MqEndpoint,
                                                   update_claim_contentions_endpoint: MqEndpoint,
                                                   cancel_claim_endpoint: MqEndpoint):
        get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
        put_tsoj_endpoint.set_responses([response_200])
        update_claim_contentions_endpoint.set_responses([response_200])
        cancel_claim_endpoint.set_responses([response_500])

        async with AsyncClient(app=app, base_url="http://test") as client:
            job_id = await self.submit_request(client)

            response = await client.get(url=f"/merge/{job_id}")
            assert response.status_code == 200

            response_json = response.json()
            assert response_json is not None
            assert response_json['job']['pending_claim_id'] == self.pending_claim_id
            assert response_json['job']['ep400_claim_id'] == self.ep400_claim_id
            assert response_json['job']['state'] == JobState.COMPLETED_ERROR.value
            assert response_json['job']['error_state'] == JobState.RUNNING_CANCEL_EP400_CLAIM.value
