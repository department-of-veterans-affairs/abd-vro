import pytest

from assessclaimcancer.src.lib import main


@pytest.mark.parametrize(
    "request_body, response",
    [
        (
                {
                    "evidence": {
                        "medications": [
                            {
                                "description": "0.4 ML Otrexup 56.3 MG/ML Auto-Injector",
                                "status": "active",
                                "authoredOn": "1952-04-06T04:00:00Z",
                            },
                            {
                                "authoredOn": "1962-04-06T04:00:00Z",
                                "description": "CISplatin 50 MG",
                                "status": "active",
                            },
                        ],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                {
                    "evidence": {
                        "medications": [
                            {
                                "authoredOn": "1962-04-06T04:00:00Z",
                                "description": "CISplatin 50 MG",
                                "status": "active",
                            },
                            {
                                "description": "0.4 ML Otrexup 56.3 MG/ML Auto-Injector",
                                "status": "active",
                                "authoredOn": "1952-04-06T04:00:00Z",
                            }
                        ],
                        "conditions": [
                            {"code": "C41.0",
                             'suggestedCategory': 'Bone',
                             "text": "Malignant neoplasm of bones of skull and face"}
                        ]
                    },
                    "evidenceSummary": {"relevantMedCount": 2, "totalMedCount": 2, "conditionsCount": 1,
                                        "relevantConditionsCount": 1},
                },
        )
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
    api_response = main.assess_cancer(request_body)

    assert api_response == response
