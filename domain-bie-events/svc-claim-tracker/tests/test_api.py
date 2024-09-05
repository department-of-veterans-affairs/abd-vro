import pytest
from fastapi.testclient import TestClient
from sqlalchemy.exc import SQLAlchemyError

from app.api import CONNECT_TO_DATABASE_FAILURE

CLAIM = 'track/v1/claim'
HEALTHCHECK = '/health'


@pytest.mark.parametrize(
    'db_ready',
    [
        pytest.param(False, id='db down'),
        pytest.param(True, id='db ready'),
    ],
)
def test_health(client: TestClient, mocker, db_ready):
    mocker.patch('app.api.tracked_claim_repo.is_ready', return_value=db_ready)

    response = client.get(HEALTHCHECK)

    json = response.json()
    if db_ready:
        assert response.status_code == 200
        assert json['status'] == 'healthy'
    else:
        assert response.status_code == 500
        assert json['status'] == 'unhealthy'
        errors = json['errors']
        if not db_ready:
            assert CONNECT_TO_DATABASE_FAILURE in errors


class TestTrackClaim:
    @pytest.fixture
    def tracked_claim_json(self):
        return {'claim_id': 1, 'established_at': '2024-01-01T00:00:00', 'feature_name': 'feature', 'feature_enabled': True}

    def test_track_claim(self, client: TestClient, tracked_claim_json):
        response = client.post(CLAIM, json=tracked_claim_json)

        assert response.status_code == 202
        json = response.json()
        assert json['id'] is not None
        assert json['created_at'] is not None
        assert json['claim_id'] == tracked_claim_json['claim_id']
        assert json['established_at'] == tracked_claim_json['established_at']
        assert json['feature_name'] == tracked_claim_json['feature_name']
        assert json['feature_enabled'] == tracked_claim_json['feature_enabled']

    def test_track_claim_failure(self, client: TestClient, tracked_claim_json, mock_repo):
        mock_repo.add.side_effect = SQLAlchemyError('NOPE')

        response = client.post(CLAIM, json=tracked_claim_json)

        assert response.status_code == 500
        json = response.json()
        errors = json['errors']
        assert len(errors) == 1
        assert CONNECT_TO_DATABASE_FAILURE in errors

    def test_track_claim_health_failure(self, client: TestClient, tracked_claim_json, mock_repo):
        mock_repo.is_ready.return_value = False

        response = client.post(CLAIM, json=tracked_claim_json)

        assert response.status_code == 500
        json = response.json()
        errors = json['errors']
        assert len(errors) == 1
        assert CONNECT_TO_DATABASE_FAILURE in errors
