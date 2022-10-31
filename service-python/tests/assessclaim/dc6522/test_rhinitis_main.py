import pytest

from assessclaimdc6522.src.lib import main


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
                    ],
                    "conditions": [],
                    "procedures": []
                },
                "date_of_claim": "2021-11-09",
            },
            {"evidence": {"medications": [{"authoredOn": "1952-04-06T04:00:00Z",
                                           "conditionRelated": "false",
                                           "description": "Prednisone",
                                           "status": "active",
                                           "suggestedCategory": []}],
                          "conditions": [],
                          "procedures": []
                          },
             "evidenceSummary": {"diagnosticCodes": [],
                                 "relevantConditionsCount": 0,
                                 "relevantMedCount": 0,
                                 "relevantProceduresCount": 0,
                                 "totalConditionsCount": 0,
                                 "totalMedCount": 1,
                                 "totalProceduresCount": 0}}
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
                    ],
                    "conditions": [],
                    "procedures": []
                },
                "date_of_claim": "2021-11-09",
            },
            {"evidence": {"medications": [{"authoredOn": "1952-04-06T04:00:00Z",
                                           "conditionRelated": "false",
                                           "description": "predniSONE 1 MG Oral Tablet",
                                           "status": "active",
                                           "suggestedCategory": []}],
                          "conditions": [],
                          "procedures": []
                          },
             "evidenceSummary": {"diagnosticCodes": [],
                                 "relevantConditionsCount": 0,
                                 "relevantMedCount": 0,
                                 "relevantProceduresCount": 0,
                                 "totalConditionsCount": 0,
                                 "totalMedCount": 1,
                                 "totalProceduresCount": 0}}
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
                    "conditions": [{"text": "Granulomatous rhinitis (chronic)",
                                    "code": "J31.0",
                                    "status": "Active"
                                    }],
                    "procedures": []
                },
                "date_of_claim": "2021-11-09",
            },
            {
                "evidence": {
                    "medications": [{
                        "authoredOn": "1952-04-06T04:00:00Z",
                        "conditionRelated": "false",
                        "description": "Advil",
                        "status": "active",
                        "suggestedCategory": []},
                        ],
                    "conditions": [{"text": "Granulomatous rhinitis (chronic)",
                                    "code": "J31.0",
                                    "status": "Active"
                                    }],
                    "procedures": []
                },
                'evidenceSummary': {'diagnosticCodes': ['6524'],
                                    'relevantConditionsCount': 1,
                                    'relevantMedCount': 0,
                                    'relevantProceduresCount': 0,
                                    'totalConditionsCount': 1,
                                    'totalMedCount': 1,
                                    'totalProceduresCount': 0}
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
    api_response = main.assess_rhinitis(request_body)

    assert api_response == response
