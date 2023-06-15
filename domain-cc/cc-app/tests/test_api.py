from fastapi.testclient import TestClient

TUBERCULOSIS_CLASSIFICATION = {
    "classification_code": 6890,
    "classification_name": "Tuberculosis",
}


def test_classification(client: TestClient):
    json_post_dict = {
        "diagnostic_code": 7710,
        "claim_id": 100,
        "form526_submission_id": 500,
    }

    response = client.post("/classifier", json=json_post_dict)
    assert response.status_code == 200
    assert (
        response.json()["classification_code"]
        == TUBERCULOSIS_CLASSIFICATION["classification_code"]
    )
    assert (
        response.json()["classification_name"]
        == TUBERCULOSIS_CLASSIFICATION["classification_name"]
    )


def test_missing_params(client: TestClient):
    """should fail if all required params are not present"""
    json_post_dict = {
        "diagnostic_code": 6510,
    }

    response = client.post("/classifier", json=json_post_dict)
    assert response.status_code == 422


def test_unmapped_diagnostic_code(client: TestClient):
    """should return null"""
    json_post_dict = {
        "diagnostic_code": 7,
        "claim_id": 700,
        "form526_submission_id": 777,
    }

    response = client.post("/classifier", json=json_post_dict)
    assert response.status_code == 200
    assert response.json() is None
