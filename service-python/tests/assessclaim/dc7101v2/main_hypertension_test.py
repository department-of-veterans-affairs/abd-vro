import pytest
from assessclaimdc7101v2.src.lib import main


@pytest.mark.parametrize(
    "request_body, response",
    [
        # All three calculator functions return valid results readings
        (
                {
                    "evidence": {
                        "bp_readings": [
                            {
                                "diastolic": {
                                    "code": "8462-4",
                                    "display": "Diastolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 115
                                },
                                "systolic": {
                                    "code": "8480-6",
                                    "display": "Systolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 180
                                },
                                "date": "2021-11-01",
                                "practitioner": "DR. JANE460 DOE922 MD",
                                "organization": "LYONS VA MEDICAL CENTER"
                            },
                            {
                                "diastolic": {
                                    "code": "8462-4",
                                    "display": "Diastolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 110
                                },
                                "systolic": {
                                    "code": "8480-6",
                                    "display": "Systolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 200.0
                                },

                                "date": "2021-09-01",
                                "practitioner": "DR. JANE460 DOE922 MD",
                                "organization": "LYONS VA MEDICAL CENTER"
                            },

                        ],
                        "conditions": [],
                        "procedures": [],
                        "medications": [{"description": "Capoten",
                                         "status": "active",
                                         "authoredOn": "1950-04-06T04:00:00Z"}],
                    },
                    "dateOfClaim": "2021-11-09",
                    "disabilityActionType": "NEW"
                }
                ,
                {"dateOfClaim": "2021-11-09",
                 "disabilityActionType": "NEW",
                 "evidence": {"bp_readings": [{"date": "2021-11-01",
                                               "diastolic": {"code": "8462-4",
                                                             "display": "Diastolic blood "
                                                                        "pressure",
                                                             "unit": "mm[Hg]",
                                                             "value": 115},
                                               "organization": "LYONS VA MEDICAL CENTER",
                                               "practitioner": "DR. JANE460 DOE922 MD",
                                               "systolic": {"code": "8480-6",
                                                            "display": "Systolic blood "
                                                                       "pressure",
                                                            "unit": "mm[Hg]",
                                                            "value": 180}},
                                              {"date": "2021-09-01",
                                               "diastolic": {"code": "8462-4",
                                                             "display": "Diastolic blood "
                                                                        "pressure",
                                                             "unit": "mm[Hg]",
                                                             "value": 110},
                                               "organization": "LYONS VA MEDICAL CENTER",
                                               "practitioner": "DR. JANE460 DOE922 MD",
                                               "systolic": {"code": "8480-6",
                                                            "display": "Systolic blood "
                                                                       "pressure",
                                                            "unit": "mm[Hg]",
                                                            "value": 200.0}}],
                              "conditions": [],
                              },
                 "evidenceSummary": {"recentBpReadings": 2,
                                     "recentElevatedBpReadings": 2,
                                     "relevantConditionsCount": 0,
                                     "totalBpReadings": 2,
                                     "totalConditionsCount": 0},
                 "sufficientForFastTracking": True}
        ),
        # sufficient_to_autopopulate returns "success": False, but history_of_diastolic_bp doesn"t
        # Note that the inverse can"t happen (where history_of_diastolic_bp fails while sufficient_to_autopopulate doesn"t)
        # because the only way history_of_diastolic_bp can fail is if there are no bp readings, which would cause
        # sufficient_to_autopopulate to fail as well 
        (
                {
                    "evidence": {
                        "bp_readings": [
                            {
                                "diastolic": {
                                    "code": "8462-4",
                                    "display": "Diastolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 115
                                },
                                "systolic": {
                                    "code": "8480-6",
                                    "display": "Systolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 180
                                },
                                "date": "2020-11-01",
                                "practitioner": "DR. JANE460 DOE922 MD",
                                "organization": "LYONS VA MEDICAL CENTER"
                            },
                            {
                                "diastolic": {
                                    "code": "8462-4",
                                    "display": "Diastolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 110
                                },
                                "systolic": {
                                    "code": "8480-6",
                                    "display": "Systolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 200
                                },
                                "date": "2020-09-01",
                                "practitioner": "DR. JANE460 DOE922 MD",
                                "organization": "LYONS VA MEDICAL CENTER"
                            }
                        ],
                        "conditions": [],
                        "medications": [],
                    },
                    "dateOfClaim": "2021-11-09",
                    "diagnosticCode": "7101v2",
                    "disabilityActionType": "NEW"
                }
                ,
                {"dateOfClaim": "2021-11-09",
                 "disabilityActionType": "NEW",
                 "evidence": {"bp_readings": [{"date": "2020-11-01",
                                               "diastolic": {"code": "8462-4",
                                                             "display": "Diastolic blood "
                                                                        "pressure",
                                                             "unit": "mm[Hg]",
                                                             "value": 115},
                                               "organization": "LYONS VA MEDICAL CENTER",
                                               "practitioner": "DR. JANE460 DOE922 MD",
                                               "systolic": {"code": "8480-6",
                                                            "display": "Systolic blood "
                                                                       "pressure",
                                                            "unit": "mm[Hg]",
                                                            "value": 180}},
                                              {"date": "2020-09-01",
                                               "diastolic": {"code": "8462-4",
                                                             "display": "Diastolic blood "
                                                                        "pressure",
                                                             "unit": "mm[Hg]",
                                                             "value": 110},
                                               "organization": "LYONS VA MEDICAL CENTER",
                                               "practitioner": "DR. JANE460 DOE922 MD",
                                               "systolic": {"code": "8480-6",
                                                            "display": "Systolic blood "
                                                                       "pressure",
                                                            "unit": "mm[Hg]",
                                                            "value": 200}}],
                              "conditions": []},
                 "evidenceSummary": {"recentBpReadings": 2,
                                     "recentElevatedBpReadings": 2,
                                     "relevantConditionsCount": 0,
                                     "totalBpReadings": 2,
                                     "totalConditionsCount": 0
                                     },
                 "sufficientForFastTracking": True}
        ),
        # Sufficiency and history algos fail
        (

                {
                    "evidence": {
                        "bp_readings": [],
                        "medications": [],
                        "conditions": []
                    },
                    "dateOfClaim": "2021-11-09",
                    "diagnosticCode": "7101v2",
                    "disabilityActionType": "NEW"
                }
                ,
                {"dateOfClaim": "2021-11-09",
                 "disabilityActionType": "NEW",
                 "evidence": {"bp_readings": [], "conditions": []},
                 "evidenceSummary": {"recentBpReadings": 0,
                                     "recentElevatedBpReadings": 0,
                                     "relevantConditionsCount": 0,
                                     "totalBpReadings": 0,
                                     "totalConditionsCount": 0},
                 "sufficientForFastTracking": None}
        ),
        # Bad data: "diastolic" key is missing in second reading
        (
                {
                    "evidence": {
                        "bp_readings": [
                            {
                                "diastolic": {
                                    "code": "8462-4",
                                    "display": "Diastolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 115
                                },
                                "systolic": {
                                    "code": "8480-6",
                                    "display": "Systolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 180
                                },
                                "date": "2021-11-01",
                                "practitioner": "DR. JANE460 DOE922 MD",
                                "organization": "LYONS VA MEDICAL CENTER"
                            },
                            {
                                "diastolic": {
                                    "code": "8462-4",
                                    "display": "Diastolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 110
                                },
                                "date": "2021-09-01",
                                "practitioner": "DR. JANE460 DOE922 MD",
                                "organization": "LYONS VA MEDICAL CENTER"
                            }
                        ],
                        "conditions": [],
                    }
                },
                {"errorMessage": "error validating request message data"}
        ),
        # Bad data:
        # - "diastolic" value is string instead of int
        # - Medication is an array with a single element *that is an int* rather than string
        # - "veteran_is_service_connected_for_dc7101" is a string
        (
                {
                    "evidence": {
                        "bp_readings": [
                            {
                                "diastolic": {
                                    "code": "8462-4",
                                    "display": "Diastolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": "115"
                                },
                                "systolic": {
                                    "code": "8480-6",
                                    "display": "Systolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 180
                                },
                                "date": "2021-11-01",
                                "practitioner": "DR. JANE460 DOE922 MD",
                                "organization": "LYONS VA MEDICAL CENTER"
                            },
                            {
                                "diastolic": {
                                    "code": "8462-4",
                                    "display": "Diastolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 110
                                },
                                "systolic": {
                                    "code": "8480-6",
                                    "display": "Systolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 200
                                },
                                "date": "2021-09-01",
                                "practitioner": "DR. JANE460 DOE922 MD",
                                "organization": "LYONS VA MEDICAL CENTER"
                            }
                        ],
                        "medications": [{"description": 11,
                                         "status": "active",
                                         "authoredOn": "1950-04-06T04:00:00Z"}]
                    },
                    "dateOfClaim": "2021-11-09"
                },
                {"errorMessage": "error validating request message data"}
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
    api_response = main.assess_hypertension(request_body)

    assert api_response == response
