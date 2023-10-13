import uuid
from unittest.mock import Mock

import pytest
from fastapi.testclient import TestClient
from model.merge_job import JobState
from src.python_src.api import job_store

MERGE = "/merge"


@pytest.fixture(autouse=True)
def mock_background_tasks(mocker):
    mocker.patch(
        'src.python_src.api.start_job_state_machine',
        return_value=Mock()
    )


@pytest.fixture(autouse=True)
def _job_store():
    job_store.clear()


def test_health(client: TestClient):
    response = client.get("/health")
    assert response.status_code == 200


@pytest.mark.parametrize("req", [
    pytest.param('{}', id="missing params"),
    pytest.param('{"supp_claim_id": 1}', id="missing pending claim id"),
    pytest.param('{"pending_claim_id": 1}', id="missing supp claim id"),
    pytest.param('{"pending_claim_id": 1,"supp_claim_id": "2"}', id="non int value"),
])
def test_invalid_requests(req, client: TestClient):
    response = client.post(MERGE, json=req)
    assert response.status_code == 422


def test_merge_claims_with_request_has_matching_claim_ids(client: TestClient):
    request = {
        "pending_claim_id": 1,
        "supp_claim_id": 1
    }

    response = client.post(MERGE, json=request)
    assert response.status_code == 400


def test_merge_claims_ok(client: TestClient):
    request = {
        "pending_claim_id": 1,
        "supp_claim_id": 2
    }

    response = client.post(MERGE, json=request)
    assert response.status_code == 202
    response_json = response.json()

    job = response_json['job']
    assert job is not None
    assert job['job_id'] is not None
    assert job['pending_claim_id'] == 1
    assert job['supp_claim_id'] == 2
    assert job['state'] == JobState.PENDING.value


def test_get_job_by_job_id_job_not_found(client: TestClient):
    response = client.get(MERGE + f'/{uuid.uuid4()}')
    assert response.status_code == 404


def test_get_job_by_job_id_job_found(client: TestClient):
    job_id = make_merge_request(client)

    response = client.get(MERGE + f'/{job_id}')
    assert response.status_code == 200
    job = response.json()['job']
    assert job['job_id'] == job_id
    assert job['pending_claim_id'] == 1
    assert job['supp_claim_id'] == 2
    assert job['state'] == JobState.PENDING.value


def make_merge_request(client: TestClient):
    request = {
        "pending_claim_id": 1,
        "supp_claim_id": 2
    }
    response_json = client.post(MERGE, json=request).json()
    job_id = response_json['job']['job_id']
    return job_id


def test_get_all_jobs(client: TestClient):
    expected_job_ids = [make_merge_request(client), make_merge_request(client)]

    response = client.get(MERGE)
    assert response.status_code == 200

    response_json = response.json()
    results = response_json['jobs']
    assert len(results) == len(expected_job_ids)

    for job in results:
        assert job['job_id'] in expected_job_ids
        assert job['pending_claim_id'] == 1
        assert job['supp_claim_id'] == 2
        assert job['state'] == JobState.PENDING.value
