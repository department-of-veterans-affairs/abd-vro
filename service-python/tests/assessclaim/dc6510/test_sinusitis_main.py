import pytest
from assessclaimdc6510.src.lib import main


@pytest.mark.parametrize(
    "request_body, response",
    [
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "azithromycin 250 MG Oral Tablet [Zithromax]",
                            "status": "active",
                            "authoredOn": "1952-04-06T04:00:00Z",
                        }
                    ],
                    "conditions": [],
                    "procedure": []
                },
                "date_of_claim": "2021-11-09",
            },
            {
                "evidence": {
                    "medications": [
                        {
                            "conditionRelated": "true",
                            "authoredOn": "1952-04-06T04:00:00Z",
                            "description": "azithromycin 250 MG Oral Tablet [Zithromax]",
                            "status": "active",
                        }
                    ],
                    "conditions": [],
                    "procedure": []
                },
                "evidenceSummary": {"relevantMedCount": 1, "totalMedCount": 1},
            },
        ),
        # demonstrates ability to match substrings in medication["text"] property
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "azithromycin 250 MG Oral Tablet [Zithromax]",
                            "status": "active",
                            "authoredOn": "1952-04-06T04:00:00Z",
                        }
                    ],
                    "conditions": [],
                    "procedure": []
                },
                "date_of_claim": "2021-11-09",
            },
            {
                "evidence": {
                    "medications": [
                        {
                            "conditionRelated": "true",
                            "authoredOn": "1952-04-06T04:00:00Z",
                            "description": "azithromycin 250 MG Oral Tablet [Zithromax]",
                            "status": "active",
                        }
                    ],
                    "conditions": [],
                    "procedure": []
                },
                "evidenceSummary": {"relevantMedCount": 1, "totalMedCount": 1},
            },
        ),
        # calculator feild mild-persistent-asthma-or-greater is True
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "Advil",
                            "status": "active",
                            "authoredOn": "1952-04-06T04:00:00Z",
                        }
                    ],
                    "conditions": [],
                    "procedure": []
                },
                "date_of_claim": "2021-11-09",
            },
            {
                "evidence": {
                    "medications": [
                        {
                            "conditionRelated": "false",
                            "authoredOn": "1952-04-06T04:00:00Z",
                            "description": "Advil",
                            "status": "active",
                        }
                    ],
                    "conditions": [],
                    "procedure": []
                },
                "evidenceSummary": {"relevantMedCount": 0, "totalMedCount": 1},
            },
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
    api_response = main.assess_sinusitis(request_body)

    assert api_response == response
