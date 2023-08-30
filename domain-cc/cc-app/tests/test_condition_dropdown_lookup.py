""" Tests for the addition of new condition dropdown mappings to the /classifier endpoint """

from fastapi.testclient import TestClient

from .conftest import TUBERCULOSIS_CLASSIFICATION


def test_diagnostic_code_mapping(client: TestClient):
    """diagnostic code mapping still works the same as v1"""
    json_post_dict = {
        "diagnostic_code": TUBERCULOSIS_CLASSIFICATION["diagnostic_code"],
        "claim_id": 100,
        "form526_submission_id": 500,
        "contention_text": "this_is_a_test",
        "claim_type": "claim_for_increase",
    }

    response = client.post("/classifier", json=json_post_dict)
    assert response.status_code == 200
    assert (
        response.json()["classification_code"]
        == TUBERCULOSIS_CLASSIFICATION["classification_code"]
    )


def test_classification_dropdown_cfi(client: TestClient):
    """classifier will fall back to dropdown lookup table if diagnostic code is not found"""
    json_post_dict = {
        "diagnostic_code": 999999999,
        "claim_id": 100,
        "form526_submission_id": 500,
        "contention_text": "Tuberculosis",
        "claim_type": "claim_for_increase",
    }

    response = client.post("/classifier", json=json_post_dict)
    assert response.status_code == 200
    assert (
        response.json()["classification_code"]
        == TUBERCULOSIS_CLASSIFICATION["classification_code"]
    )


def test_dropdown_lut_case_insensitive(client: TestClient):
    """dropdown lookup table is case insensitive"""
    json_post_dict = {
        "claim_id": 700,
        "form526_submission_id": 777,
        "claim_type": "new",
        "contention_text": "tUbeRcUloSis",
    }

    response = client.post("/classifier", json=json_post_dict)
    assert response.status_code == 200
    assert (
        response.json()["classification_code"]
        == TUBERCULOSIS_CLASSIFICATION["classification_code"]
    )


def test_dropdown_lut_whitespace(client: TestClient):
    """dropdown lookup table doesn't care about whitespace"""
    json_post_dict = {
        "claim_id": 700,
        "form526_submission_id": 777,
        "claim_type": "new",
        "contention_text": "    tuberculosis  ",
    }

    response = client.post("/classifier", json=json_post_dict)
    assert response.status_code == 200
    assert (
        response.json()["classification_code"]
        == TUBERCULOSIS_CLASSIFICATION["classification_code"]
    )


def test_classification_dropdown_new(client: TestClient):
    json_post_dict = {
        "claim_id": 100,
        "form526_submission_id": 500,
        "contention_text": "Tuberculosis",
        "claim_type": "new",
    }

    response = client.post("/classifier", json=json_post_dict)
    assert response.status_code == 200
    assert (
        response.json()["classification_code"]
        == TUBERCULOSIS_CLASSIFICATION["classification_code"]
    )


def test_null_response(client: TestClient):
    """if both LUT's fail to map to classification, return null"""
    json_post_dict = {
        "diagnostic_code": 7,
        "claim_id": 700,
        "form526_submission_id": 777,
        "claim_type": "claim_for_increase",
        "contention_text": "this_is_a_test",
    }

    response = client.post("/classifier", json=json_post_dict)
    assert response.status_code == 200
    assert response.json() is None
