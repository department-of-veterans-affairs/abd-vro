import uuid
from datetime import datetime
from unittest.mock import MagicMock, Mock

import pytest
from api import CONNECT_TO_DATABASE_FAILURE, CONNECT_TO_RABBIT_MQ_FAILURE
from fastapi.testclient import TestClient
from model.merge_job import MergeJob
from schema.merge_job import JobState
from sqlalchemy.exc import SQLAlchemyError

MERGE = "/merge"
JOB_ID = uuid.uuid4()
TIME = datetime.now()


@pytest.fixture(autouse=True)
def mock_uuid(mocker):
    mocker.patch('src.python_src.api.uuid4', return_value=JOB_ID)


@pytest.fixture(autouse=True)
def mock_background_tasks(mocker):
    mocker.patch('src.python_src.api.JOB_RUNNER.start_job', return_value=Mock())


@pytest.fixture(autouse=True)
def mock_hoppy(mocker):
    mock_hoppy = Mock()
    mock_hoppy.is_ready = Mock(return_value=True)
    return mocker.patch('src.python_src.api.HOPPY', return_value=mock_hoppy)


@pytest.fixture(autouse=True)
def mock_job_store(mocker):
    job_store_mock = Mock()
    mock_job_store.submit_merge_job = Mock()
    mock_job_store.update_merge_job = Mock()
    mock_job_store.get_merge_job = MagicMock()
    mock_job_store.is_ready = Mock(return_value=True)
    return mocker.patch('src.python_src.api.JOB_STORE', return_value=job_store_mock)


def submitted_job():
    return MergeJob(
        job_id=JOB_ID,
        pending_claim_id=1,
        ep400_claim_id=2,
        state=JobState.PENDING.value,
        created_at=TIME,
        updated_at=TIME)


@pytest.mark.parametrize("hoppy_ready, db_ready", [
    pytest.param(True, True, id="all services up"),
    pytest.param(True, False, id="db is down"),
    pytest.param(False, True, id="hoppy is down"),
])
def test_health(client: TestClient, mocker, hoppy_ready, db_ready):
    mocker.patch('src.python_src.api.HOPPY.is_ready', return_value=hoppy_ready)
    mocker.patch('src.python_src.api.JOB_STORE.is_ready', return_value=db_ready)

    response = client.get("/health")

    json = response.json()
    if hoppy_ready and db_ready:
        assert response.status_code == 200
        assert json['status'] == 'healthy'
    else:
        assert response.status_code == 500
        assert json['status'] == 'unhealthy'
        errors = json['errors']
        if not hoppy_ready:
            assert CONNECT_TO_RABBIT_MQ_FAILURE in errors
        if not db_ready:
            assert CONNECT_TO_DATABASE_FAILURE in errors


@pytest.mark.parametrize("req", [
    pytest.param('{}', id="missing params"),
    pytest.param('{"ep400_claim_id": 1}', id="missing pending claim id"),
    pytest.param('{"pending_claim_id": 1}', id="missing EP400 claim id"),
    pytest.param('{"pending_claim_id": 1,"ep400_claim_id": "2"}', id="non int value"),
])
def test_invalid_requests(req, client: TestClient):
    response = client.post(MERGE, json=req)
    assert response.status_code == 422


def test_merge_claims_with_request_has_matching_claim_ids(client: TestClient):
    request = {
        "pending_claim_id": 1,
        "ep400_claim_id": 1
    }

    response = client.post(MERGE, json=request)
    assert response.status_code == 400


def test_merge_claims_ok(client: TestClient, mock_job_store):
    request = {
        "pending_claim_id": 1,
        "ep400_claim_id": 2
    }

    response = client.post(MERGE, json=request)
    assert response.status_code == 202
    response_json = response.json()

    job = response_json['job']
    assert job is not None
    assert job['job_id'] == str(JOB_ID)
    assert job['pending_claim_id'] == 1
    assert job['ep400_claim_id'] == 2
    assert job['state'] == JobState.PENDING.value
    assert job['created_at'] is not None
    assert job['updated_at'] is not None


@pytest.mark.parametrize("hoppy_ready, db_ready", [
    pytest.param(False, False, id="both are down"),
    pytest.param(True, False, id="db is down"),
    pytest.param(False, True, id="hoppy is down"),
])
def test_merge_claims_while_unhealthy(client: TestClient, mocker, hoppy_ready, db_ready):
    mocker.patch('src.python_src.api.HOPPY.is_ready', return_value=hoppy_ready)
    mocker.patch('src.python_src.api.JOB_STORE.is_ready', return_value=db_ready)
    request = {
        "pending_claim_id": 1,
        "ep400_claim_id": 2
    }

    response = client.post(MERGE, json=request)
    assert response.status_code == 500
    json = response.json()
    errors = json['errors']
    if not hoppy_ready:
        assert CONNECT_TO_RABBIT_MQ_FAILURE in errors
    if not db_ready:
        assert CONNECT_TO_DATABASE_FAILURE in errors


def test_get_job_by_job_id_job_not_found(client: TestClient, mock_job_store):
    mock_job_store.get_merge_job.return_value = None
    response = client.get(MERGE + f'/{uuid.uuid4()}')
    assert response.status_code == 404


def test_get_job_by_job_id_job_found(client: TestClient, mock_job_store):
    job_id = make_merge_request(client)
    mock_job_store.get_merge_job.return_value = submitted_job()

    response = client.get(MERGE + f'/{job_id}')
    assert response.status_code == 200
    job = response.json()['job']
    assert job['job_id'] == str(JOB_ID)
    assert job['pending_claim_id'] == 1
    assert job['ep400_claim_id'] == 2
    assert job['state'] == JobState.PENDING.value
    assert job['created_at'] == TIME.isoformat()
    assert job['updated_at'] == TIME.isoformat()


def test_get_job_by_job_id_when_unhealthy(client: TestClient, mock_job_store):
    mock_job_store.get_merge_job.side_effect = SQLAlchemyError()
    response = client.get(MERGE + f'/{JOB_ID}')
    assert response.status_code == 500
    json = response.json()
    errors = json['errors']
    assert len(errors) == 1
    assert CONNECT_TO_DATABASE_FAILURE in errors


def make_merge_request(client: TestClient):
    request = {
        "pending_claim_id": 1,
        "ep400_claim_id": 2
    }
    response_json = client.post(MERGE, json=request).json()
    job_id = response_json['job']['job_id']
    return job_id


@pytest.mark.parametrize("page,size",
                         [
                             pytest.param(None, None, id="defaults"),
                             pytest.param(1, None, id="first page, 10 items"),
                             pytest.param(1, None, id="last page, 1 item"),
                             pytest.param(1, 2, id="first page, 2 items"),
                             pytest.param(6, 2, id="last page, 2 items"),
                         ])
def test_get_merge_jobs_pagination(client: TestClient, mock_job_store, page, size):
    # Set defaults for missing values:
    expected_page = page if page else 1
    expected_size = size if size else 10

    expected_jobs = [submitted_job() for i in range(expected_size)]
    mock_job_store.query = Mock(return_value=(expected_jobs, 11))

    params = {}
    if page:
        params['page'] = page
    if size:
        params['size'] = size

    response = client.get(MERGE, params=params)
    assert response.status_code == 200

    response_json = response.json()
    assert response_json['total'] == 11
    assert response_json['states'] == JobState.incomplete_states()
    assert response_json['page'] == expected_page
    assert response_json['size'] == expected_size
    results = response_json['jobs']
    assert len(results) == len(expected_jobs)

    for job in results:
        assert job['job_id'] == str(JOB_ID)
        assert job['pending_claim_id'] == 1
        assert job['ep400_claim_id'] == 2
        assert job['state'] == JobState.PENDING.value
        assert job['created_at'] == TIME.isoformat()
        assert job['updated_at'] == TIME.isoformat()


def test_get_merge_jobs_pagination_when_unhealthy(client: TestClient, mock_job_store):
    mock_job_store.query.side_effect = SQLAlchemyError()
    response = client.get(MERGE)
    assert response.status_code == 500
    json = response.json()
    errors = json['errors']
    assert len(errors) == 1
    assert CONNECT_TO_DATABASE_FAILURE in errors
