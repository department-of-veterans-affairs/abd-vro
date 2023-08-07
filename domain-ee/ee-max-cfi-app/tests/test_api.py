from fastapi.testclient import TestClient

MAX_RATING = "/max-ratings"

TINNITUS = {
    "diagnostic_code": 6260,
    "max_rating": 10
}

TUBERCULOSIS = {"diagnostic_code": 7710}


def test_max_rating_with_no_dc(client: TestClient):
    json_post_dict = {
        "diagnostic_codes": []
    }

    response = client.post(MAX_RATING, json=json_post_dict)
    assert response.status_code == 422


def test_max_rating_with_one_dc(client: TestClient):
    json_post_dict = {
        "diagnostic_codes": [
            TINNITUS["diagnostic_code"]
        ]
    }

    response = client.post(MAX_RATING, json=json_post_dict)
    assert response.status_code == 200
    response_json = response.json()

    ratings = response_json["ratings"]
    assert (len(ratings) == 1)
    assert (ratings[0]["diagnostic_code"] == TINNITUS["diagnostic_code"])
    assert (ratings[0]["max_rating"] == TINNITUS["max_rating"])


def test_max_rating_with_duplicate_dc(client: TestClient):
    json_post_dict = {
        "diagnostic_codes": [
            TINNITUS["diagnostic_code"],
            TINNITUS["diagnostic_code"]
        ]
    }

    response = client.post(MAX_RATING, json=json_post_dict)
    assert response.status_code == 422


def test_max_rating_with_unmapped_dc(client: TestClient):
    json_post_dict = {
        "diagnostic_codes": [
            TUBERCULOSIS["diagnostic_code"]
        ]
    }

    response = client.post(MAX_RATING, json=json_post_dict)

    assert response.status_code == 200
    response_json = response.json()

    ratings = response_json["ratings"]
    assert (len(ratings) == 0)


def test_max_rating_with_value_below_range(client: TestClient):
    json_post_dict = {
        "diagnostic_codes": [
            4999
        ]
    }

    response = client.post(MAX_RATING, json=json_post_dict)
    assert response.status_code == 400


def test_max_rating_with_value_above_range(client: TestClient):
    json_post_dict = {
        "diagnostic_codes": [
            10001
        ]
    }

    response = client.post(MAX_RATING, json=json_post_dict)
    assert response.status_code == 400


def test_missing_params(client: TestClient):
    """should fail if all required params are not present"""
    json_post_dict = {}

    response = client.post(MAX_RATING, json=json_post_dict)
    assert response.status_code == 422


def test_unprocessable_content(client: TestClient):
    json_post_dict = {
        "diagnostic_codes": 6510,  # Should be an array
    }

    response = client.post(MAX_RATING, json=json_post_dict)
    assert response.status_code == 422
