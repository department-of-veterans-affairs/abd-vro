from fastapi.testclient import TestClient


def test_classification(client: TestClient):
    json_post_dict = {
        "diagnostic_code": 6510,
        "claim_id": 100,
        "form526_submission_id": 500,
    }

    response = client.post("/classifier", json=json_post_dict)
    assert response.status_code == 201

def test_missing_params(client: TestClient):
    """ should fail if all required params are not present """
    json_post_dict = {
        "diagnostic_code": 6510,
    }

    response = client.post("/classifier", json=json_post_dict)
    assert response.status_code == 422
