from fastapi.testclient import TestClient

TUBERCULOSIS_CLASSIFICATION = {
    "diagnostic_code": 7710,
    "classification_code": 6890,
    "classification_name": "Tuberculosis",
}
BENIGN_GROWTH_BRAIN_CLASSIFICATION = {
    "diagnostic_code": 8003,
    "classification_code": 8964,
    "classification_name": "Cyst/Benign Growth - Neurological other System",
}
DRUG_INDUCED_PULMONARY_PNEMONIA_CLASSIFICATION = {
    "diagnostic_code": 6829,
    "classification_code": 9012,
    "classification_name": "Respiratory",
}


def test_classification(client: TestClient):
    json_post_dict = {
        "diagnostic_code": TUBERCULOSIS_CLASSIFICATION["diagnostic_code"],
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


def test_unprocessable_content(client: TestClient):
    json_post_dict = {
        "diagnostic_code": "this is personal information",
        "claim_id": "SQL \n injection \n not really",
        "form526_submission_id": "1-234-567-9999",
    }

    response = client.post("/classifier", json=json_post_dict)
    assert response.status_code == 422


def test_v2_table_diagnostic_code(client: TestClient):
    json_post_dict = {
        "diagnostic_code": BENIGN_GROWTH_BRAIN_CLASSIFICATION["diagnostic_code"],
        "claim_id": 123,
        "form526_submission_id": 456,
    }

    response = client.post("/classifier", json=json_post_dict)
    assert response.status_code == 200
    assert (
        response.json()["classification_code"]
        == BENIGN_GROWTH_BRAIN_CLASSIFICATION["classification_code"]
    )
    assert (
        response.json()["classification_name"]
        == BENIGN_GROWTH_BRAIN_CLASSIFICATION["classification_name"]
    )

def test_v3_table_diagnostic_code(client: TestClient):
    json_post_dict = {
        "diagnostic_code": DRUG_INDUCED_PULMONARY_PNEMONIA_CLASSIFICATION["diagnostic_code"],
        "claim_id": 123,
        "form526_submission_id": 456,
    }

    response = client.post("/classifier", json=json_post_dict)
    assert response.status_code == 200
    assert (
        response.json()["classification_code"]
        == DRUG_INDUCED_PULMONARY_PNEMONIA_CLASSIFICATION["classification_code"]
    )
    assert (
        response.json()["classification_name"]
        == DRUG_INDUCED_PULMONARY_PNEMONIA_CLASSIFICATION["classification_name"]
    )

6829