import json

import pytest
from src.lib import main


@pytest.mark.parametrize(
    "request_body, response",
    [
        (
            {
                "medication": [{"text": "Prednisone"}],
                "date_of_claim": "2021-11-09",
                "vasrd": "6602"  
            },
            {"body": json.dumps({"evidence": {"medication": [{"text": "Prednisone"}]}})}
        ),
        (
            {
                "medication": [{"text" : "Advil"}],
                "date_of_claim": "2021-11-09",
                "vasrd": "6602"  
            },
            {"body": json.dumps({"evidence": {"medication": []}})}
        ),
    ],
)
def test_main(request_body, response):
    """
    Test the function that takes the request and returns the response

    :param request_body: request body with blood pressure readings and other data
    :type request_body: dict
    :param response: response after running data through algorithms
    :type response: dict
    """
    api_response = main.assess_asthma(request_body)

    assert json.loads(api_response["body"]) == json.loads(response["body"])
