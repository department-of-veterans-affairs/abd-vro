import pytest

from assessclaimdc6602v2.src.lib import main


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
                                "dataSource": "MAS"
                            }
                        ],
                        "conditions": [],
                        "procedures": []
                    },
                    "claimSubmissionDateTime": "2021-11-09",
                    "claimSubmissionId": "1234",
                    "disabilityActionType": "INCREASE"
                },
                {"claimSubmissionDateTime": "2021-11-09",
                 "claimSubmissionId": "1234",
                 "disabilityActionType": "INCREASE",
                 "evidence": {"conditions": [],
                              "documentsWithoutAnnotationsChecked": [],
                              "medications": [{"authoredOn": "1952-04-06T04:00:00Z",
                                               "classification": "Anti-Inflammatory/Corticosteroid/Immuno-Suppressive",
                                               "dataSource": "MAS",
                                               "dateFormatted": "4/6/1952",
                                               "description": "Prednisone",
                                               "status": "active"}],
                              "procedures": []},
                 "evidenceSummary": {"proceduresCount": 0,
                                     "relevantConditionsLighthouseCount": 0,
                                     "schedularMedicationOneYearCount": 0,
                                     "totalConditionsCount": 0,
                                     "totalMedCount": 1}}
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
                                "dataSource": "MAS"
                            }
                        ],
                        "conditions": [],
                        "procedures": []
                    },
                    "claimSubmissionDateTime": "2021-11-09",
                    "claimSubmissionId": "1234",
                    "disabilityActionType": "INCREASE"
                },
                {"claimSubmissionDateTime": "2021-11-09",
                 "claimSubmissionId": "1234",
                 "disabilityActionType": "INCREASE",
                 "evidence": {"conditions": [],
                              "documentsWithoutAnnotationsChecked": [],
                              "medications": [{"authoredOn": "1952-04-06T04:00:00Z",
                                               "classification": "Anti-Inflammatory/Corticosteroid/Immuno-Suppressive",
                                               "dataSource": "MAS",
                                               "dateFormatted": "4/6/1952",
                                               "description": "predniSONE 1 MG Oral Tablet",
                                               "status": "active"}],
                              "procedures": []},
                 "evidenceSummary": {"proceduresCount": 0,
                                     "relevantConditionsLighthouseCount": 0,
                                     "schedularMedicationOneYearCount": 0,
                                     "totalConditionsCount": 0,
                                     "totalMedCount": 1}}
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
                                "dataSource": "MAS"
                            }
                        ],
                        "conditions": [],
                        "procedures": []
                    },
                    "claimSubmissionDateTime": "2021-11-09",
                    "claimSubmissionId": "1234",
                    "disabilityActionType": "INCREASE"
                },
                {"claimSubmissionDateTime": "2021-11-09",
                 "claimSubmissionId": "1234",
                 "disabilityActionType": "INCREASE",
                 "evidence": {"conditions": [],
                              "documentsWithoutAnnotationsChecked": [],
                              "medications": [{"authoredOn": "1952-04-06T04:00:00Z",
                                               "classification": "",
                                               "dataSource": "MAS",
                                               "dateFormatted": "4/6/1952",
                                               "description": "Advil",
                                               "status": "active"}],
                              "procedures": []},
                 "evidenceSummary": {"proceduresCount": 0,
                                     "relevantConditionsLighthouseCount": 0,
                                     "schedularMedicationOneYearCount": 0,
                                     "totalConditionsCount": 0,
                                     "totalMedCount": 1}}
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
    api_response = main.assess_sufficiency_asthma(request_body)

    assert api_response == response
