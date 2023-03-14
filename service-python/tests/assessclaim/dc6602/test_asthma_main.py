from unittest.mock import Mock

import pytest

from assessclaimdc6602.src.lib import main


@pytest.mark.parametrize(
    "request_body, response",
    [
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "Prednisone",
                            "status": "active",
                            "authoredOn": "1952-04-06T04:00:00Z",
                        }
                    ]
                },
                "dateOfClaim": "2021-11-09",
                "claimSubmissionId": "1234"
            },
            {
                "evidence": {
                    "medications": [
                        {
                            "asthmaRelevant": "true",
                            "authoredOn": "1952-04-06T04:00:00Z",
                            "description": "Prednisone",
                            "status": "active",
                        }
                    ]
                },
                "evidenceSummary": {"relevantMedCount": 1, "totalMedCount": 1},
                "claimSubmissionId": "1234"
            },
        ),
        # demonstrates ability to match substrings in medication["text"] property
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "predniSONE 1 MG Oral Tablet",
                            "status": "active",
                            "authoredOn": "1952-04-06T04:00:00Z",
                        }
                    ]
                },
                "dateOfClaim": "2021-11-09",
                "claimSubmissionId": "1234"
            },
            {
                "evidence": {
                    "medications": [
                        {
                            "asthmaRelevant": "true",
                            "authoredOn": "1952-04-06T04:00:00Z",
                            "description": "predniSONE 1 MG Oral Tablet",
                            "status": "active",
                        }
                    ]
                },
                "evidenceSummary": {"relevantMedCount": 1, "totalMedCount": 1},
                "claimSubmissionId": "1234"
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
                    ]
                },
                "dateOfClaim": "2021-11-09",
                "claimSubmissionId": "1234"
            },
            {
                "evidence": {
                    "medications": [
                        {
                            "asthmaRelevant": "false",
                            "authoredOn": "1952-04-06T04:00:00Z",
                            "description": "Advil",
                            "status": "active",
                        }
                    ]
                },
                "evidenceSummary": {"relevantMedCount": 0, "totalMedCount": 1},
                "claimSubmissionId": "1234"
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
    data_model = Mock(autospec=True, create=True)
    api_response = main.assess_asthma(request_body)

    assert api_response == response
