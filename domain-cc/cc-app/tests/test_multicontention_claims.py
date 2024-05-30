from fastapi.testclient import TestClient


def test_vagov_classifier_mixed_types(client: TestClient):
    """
    Tests response of multi-contention claims matches expected values
    """
    json_post_dict = {
        "claim_id": 100,
        "form526_submission_id": 500,
        "contentions": [
            {
                "contention_text": "tinnitus (ringing or hissing in ears)",
                "contention_type": "NEW",
            },
            {
                "contention_text": "asthma",
                "contention_type": "INCREASE",
                "diagnostic_code": 8550,
            },
            {
                "contention_text": "free text entry",
                "contention_type": "NEW",
                "diagnostic_code": None,
            },
        ],
    }
    response = client.post("/va-gov-claim-classifier", json=json_post_dict)
    assert response.status_code == 200
    assert response.json()["contentions"] == [
        {
            "classification_code": 3140,
            "classification_name": "Hearing Loss",
            "diagnostic_code": None,
            "contention_type": "NEW",
        },
        {
            "classification_code": 9012,
            "classification_name": "Respiratory",
            "diagnostic_code": 8550,
            "contention_type": "INCREASE",
        },
        {
            "classification_code": None,
            "classification_name": None,
            "diagnostic_code": None,
            "contention_type": "NEW",
        },
    ]


def test_vagov_classifier_empty_contentions(client: TestClient):
    """
    Tests 422 is returned when contentions is empty
    """
    json_post_dict = {
        "claim_id": 100,
        "form526_submission_id": 500,
        "contentions": [],
    }
    response = client.post("/va-gov-claim-classifier", json=json_post_dict)
    assert response.status_code == 422


def test_single_contention(client: TestClient):
    """
    Tests response of single contention claim matches expected values
    """
    json_post_dict = {
        "claim_id": 100,
        "form526_submission_id": 500,
        "contentions": [
            {
                "contention_text": "asthma",
                "contention_type": "NEW",
            },
        ],
    }
    response = client.post("/va-gov-claim-classifier", json=json_post_dict)
    assert response.status_code == 200
    assert response.json()["contentions"] == [
        {
            "classification_code": 9012,
            "classification_name": "Respiratory",
            "diagnostic_code": None,
            "contention_type": "NEW",
        }
    ]


def test_order_response(client: TestClient):
    """
    Tests to make sure that the order of the response matches the
    order of input
    """
    json_post_dict = json_post_dict = {
        "claim_id": 100,
        "form526_submission_id": 500,
        "contentions": [
            {
                "contention_text": "tinnitus (ringing or hissing in ears)",
                "contention_type": "NEW",
            },
            {
                "contention_text": "asthma",
                "contention_type": "INCREASE",
                "diagnostic_code": 8550,
            },
            {
                "contention_text": "free text entry",
                "contention_type": "NEW",
                "diagnostic_code": None,
            },
        ],
    }
    response = client.post("/va-gov-claim-classifier", json=json_post_dict)
    expected_order = [3140, 9012, None]
    for i in range(len(response.json()["contentions"])):
        assert (
            response.json()["contentions"][i]["classification_code"]
            == expected_order[i]
        )
