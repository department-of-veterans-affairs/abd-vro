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
                        "procedures": []
                    },
                    "dateOfClaim": "2021-11-09",
                    "claimSubmissionId": "1234"
                },
                {
                    "evidence": {
                        "medications": [
                            {
                                "conditionRelated": True,
                                "authoredOn": "1952-04-06T04:00:00Z",
                                "description": "azithromycin 250 MG Oral Tablet [Zithromax]",
                                "status": "active",
                            }
                        ],
                        "conditions": [],
                        "procedures": []
                    },
                    "evidenceSummary": {"relevantConditionsCount": 0,
                                        "relevantMedCount": 1,
                                        "totalConditionsCount": 0,
                                        "totalMedCount": 1,
                                        "relevantProceduresCount": 0,
                                        "totalProceduresCount": 0},
                    "sufficientForFastTracking": None,
                    "claimSubmissionId": "1234"
                }
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
                        "conditions": [{"text": "Chronic maxillary sinusitis",
                                        "code": "35923002",
                                        "status": "active",
                                        "onsetDate": "2020-11-01"}],
                        "procedures": []
                    },
                    "dateOfClaim": "2021-06-09",
                    "claimSubmissionId": "1234"
                },
                {
                    "evidence": {
                        "medications": [
                            {
                                "conditionRelated": True,
                                "authoredOn": "1952-04-06T04:00:00Z",
                                "description": "azithromycin 250 MG Oral Tablet [Zithromax]",
                                "status": "active",
                            }
                        ],
                        "conditions": [],
                        "procedures": []
                    },
                    "evidenceSummary": {"relevantConditionsCount": 0,
                                        "relevantMedCount": 1,
                                        "totalConditionsCount": 1,
                                        "totalMedCount": 1,
                                        "relevantProceduresCount": 0,
                                        "totalProceduresCount": 0},
                    "sufficientForFastTracking": None,
                    "claimSubmissionId": "1234"
                },
        ),
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
                        "conditions": [{"text": "Chronic maxillary sinusitis",
                                        "code": "35923002",
                                        "status": "active",
                                        "onsetDate": "2021-11-01"},
                                       {"text": "Chronic maxillary sinusitis",
                                        "code": "J32.0",
                                        "status": "active",
                                        "onsetDate": "2021-11-01"}],
                        "procedures": [{"text": "Nasal/sinus endoscopy, surgical, with dilation (eg, balloon "
                                                "dilation); sphenoid "
                                                "sinus ostium",
                                        "code": "31297",
                                        "status": "completed",
                                        "performedDate": "2010-2-22"}
                                       ]
                    },
                    "dateOfClaim": "2021-11-09",
                    "claimSubmissionId": "1234"
                },
                {
                    "evidence": {
                        "medications": [
                            {
                                "conditionRelated": False,
                                "authoredOn": "1952-04-06T04:00:00Z",
                                "description": "Advil",
                                "status": "active",
                            }
                        ],
                        "conditions": [
                                       {"text": "Chronic maxillary sinusitis",
                                        "code": "J32.0",
                                        "status": "active",
                                        "onsetDate": "2021-11-01"},
                                       ],
                        "procedures": [{"text": "Nasal/sinus endoscopy, surgical, with dilation (eg, balloon "
                                                "dilation); sphenoid sinus ostium",
                                        "code": "31297",
                                        "status": "completed",
                                        "performedDate": "2010-2-22"},
                                       ]
                    },
                    "evidenceSummary": {"relevantConditionsCount": 1,
                                        "relevantMedCount": 0,
                                        "totalConditionsCount": 2,
                                        "totalMedCount": 1,
                                        "relevantProceduresCount": 1,
                                        "totalProceduresCount": 1},
                    "sufficientForFastTracking": False,
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
    api_response = main.assess_sinusitis(request_body)

    assert api_response == response
