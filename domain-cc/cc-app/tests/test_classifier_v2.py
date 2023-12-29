from fastapi.testclient import TestClient

from .conftest import (
    ASTRAGALECTOMY_CLASSIFICATION,
    BENIGN_GROWTH_BRAIN_CLASSIFICATION,
    TUBERCULOSIS_CLASSIFICATION,
)


def test_single_issue_cfi(client: TestClient):
    json_post_dict = {
        "diagnostic_code": TUBERCULOSIS_CLASSIFICATION["diagnostic_code"],
        "claim_id": 100,
        "form526_submission_id": 500,
        "claim_type": "claim_for_increase",
        "contentions": [
            {
                "diagnostic_code": TUBERCULOSIS_CLASSIFICATION["diagnostic_code"],
                "contention_text": "Tuberculosis",
            }
        ],
    }

    response = client.post("/classifier/v2", json=json_post_dict)
    classification = response.json()["classifications"][0]
    assert response.status_code == 200
    assert (
        classification["classification_code"]
        == TUBERCULOSIS_CLASSIFICATION["classification_code"]
    )
    assert (
        classification["classification_name"]
        == TUBERCULOSIS_CLASSIFICATION["classification_name"]
    )


def test_dc_required_for_cfi(client: TestClient):
    json_post_dict = {
        "diagnostic_code": TUBERCULOSIS_CLASSIFICATION["diagnostic_code"],
        "claim_id": 100,
        "form526_submission_id": 500,
        "claim_type": "claim_for_increase",
        "contentions": [
            {
                "diagnostic_code": None,
                "contention_text": "Tuberculosis",
            }
        ],
    }
    response = client.post("/classifier/v2", json=json_post_dict)
    assert response.status_code == 422


def test_multi_issue_cfi(client: TestClient):
    json_post_dict = {
        "diagnostic_code": TUBERCULOSIS_CLASSIFICATION["diagnostic_code"],
        "claim_id": 100,
        "form526_submission_id": 500,
        "claim_type": "claim_for_increase",
        "contentions": [
            {
                "diagnostic_code": TUBERCULOSIS_CLASSIFICATION["diagnostic_code"],
                "contention_text": "Tuberculosis",
            },
            {
                "diagnostic_code": BENIGN_GROWTH_BRAIN_CLASSIFICATION[
                    "diagnostic_code"
                ],
                "contention_text": "benign growth stuff",
            },
        ],
    }

    response = client.post("/classifier/v2", json=json_post_dict)
    first, second = response.json()["classifications"]
    assert response.status_code == 200
    assert (
        first["classification_code"]
        == TUBERCULOSIS_CLASSIFICATION["classification_code"]
    )
    assert (
        first["classification_name"]
        == TUBERCULOSIS_CLASSIFICATION["classification_name"]
    )
    assert (
        second["classification_code"]
        == BENIGN_GROWTH_BRAIN_CLASSIFICATION["classification_code"]
    )
    assert (
        second["classification_name"]
        == BENIGN_GROWTH_BRAIN_CLASSIFICATION["classification_name"]
    )


def test_single_issue_new(client: TestClient):
    json_post_dict = {
        "diagnostic_code": TUBERCULOSIS_CLASSIFICATION["diagnostic_code"],
        "claim_id": 100,
        "form526_submission_id": 500,
        "claim_type": "new",
        "contentions": [
            {
                "contention_text": "Tuberculosis",
            }
        ],
    }
    response = client.post("/classifier/v2", json=json_post_dict)
    classification = response.json()["classifications"][0]
    assert response.status_code == 200
    assert (
        classification["classification_code"]
        == TUBERCULOSIS_CLASSIFICATION["classification_code"]
    )
    assert (
        classification["classification_name"]
        == TUBERCULOSIS_CLASSIFICATION["classification_name"]
    )


def test_multi_issue_new(client: TestClient):
    json_post_dict = {
        "diagnostic_code": TUBERCULOSIS_CLASSIFICATION["diagnostic_code"],
        "claim_id": 100,
        "form526_submission_id": 500,
        "claim_type": "new",
        "contentions": [
            {
                "contention_text": "Tuberculosis",
            },
            {
                "contention_text": "astragalectomy or talectomy (removal of talus bone in ankle), right",
            },
        ],
    }
    response = client.post("/classifier/v2", json=json_post_dict)
    first, second = response.json()["classifications"]
    assert response.status_code == 200
    assert (
        first["classification_code"]
        == TUBERCULOSIS_CLASSIFICATION["classification_code"]
    )
    assert (
        first["classification_name"]
        == TUBERCULOSIS_CLASSIFICATION["classification_name"]
    )
    assert (
        second["classification_code"]
        == ASTRAGALECTOMY_CLASSIFICATION["classification_code"]
    )
    assert (
        second["classification_name"]
        == ASTRAGALECTOMY_CLASSIFICATION["classification_name"]
    )


def test_contention_text_required(client: TestClient):
    json_post_dict = {
        "diagnostic_code": TUBERCULOSIS_CLASSIFICATION["diagnostic_code"],
        "claim_id": 100,
        "form526_submission_id": 500,
        "claim_type": "new",
        "contentions": [
            {
                "contention_text": None,
            },
        ],
    }
    response = client.post("/classifier/v2", json=json_post_dict)
    assert response.status_code == 422
