import pytest
from assessclaimdc7101.src.lib import main


@pytest.mark.parametrize(
    "request_body, response",
    [
        # New claim with two recent BP (one elevated, one normal) and relevant condition
        (
                {
                    "evidence": {
                        "bp_readings": [
                            {
                                "diastolic": {
                                    "code": "8462-4",
                                    "display": "Diastolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 90
                                },
                                "systolic": {
                                    "code": "8480-6",
                                    "display": "Systolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 120
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
                        "conditions": [{"code": "I10",
                                        "text": "Essential (primary) hypertension",
                                        "recordedDate": "1950-04-06T04:00:00Z"},
                                       {"code": "1234",
                                        "text": "snomed diagnosis"}],
                        "procedures": [],
                        "medications": [{"description": "Capoten",
                                         "status": "active",
                                         "authoredOn": "1950-04-06T04:00:00Z"}],
                    },
                    "dateOfClaim": "2021-11-09",
                    "disabilityActionType": "NEW",
                    "claimSubmissionId": "1234"
                }
                ,
                {"dateOfClaim": "2021-11-09",
                 "disabilityActionType": "NEW",
                 "evidence": {"bp_readings": [{"date": "2021-11-01",
                                               "diastolic": {"code": "8462-4",
                                                             "display": "Diastolic blood "
                                                                        "pressure",
                                                             "unit": "mm[Hg]",
                                                             "value": 90},
                                               "organization": "LYONS VA MEDICAL CENTER",
                                               "practitioner": "DR. JANE460 DOE922 MD",
                                               "systolic": {"code": "8480-6",
                                                            "display": "Systolic blood "
                                                                       "pressure",
                                                            "unit": "mm[Hg]",
                                                            "value": 120}},
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
                              "conditions": [{"code": "I10",
                                              "text": "Essential (primary) hypertension",
                                              "recordedDate": "1950-04-06T04:00:00Z",
                                              "relevant": True},
                                             {'code': '1234',
                                              'relevant': False,
                                              'text': 'snomed diagnosis'}],
                              },
                 "evidenceSummary": {"relevantConditionsCount": 1,
                                     "totalBpReadings": 2,
                                     "recentBpReadings": 2,
                                     "totalConditionsCount": 2},
                 "sufficientForFastTracking": True,
                 "claimSubmissionId": "1234"}
        ),
        # New claim with two recent BP both elevated and no condition
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
                    "disabilityActionType": "NEW",
                    "claimSubmissionId": "1234"
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
                 "evidenceSummary": {"relevantConditionsCount": 0,
                                     "totalBpReadings": 2,
                                     "recentBpReadings": 2,
                                     "totalConditionsCount": 0},
                 "sufficientForFastTracking": True,
                 "claimSubmissionId": "1234"}
        ),
        # New claim with relevant condition but no recent BP
        (
                {
                    "evidence": {
                        "bp_readings": [{"date": "2020-11-01",
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
                                                      "value": 180}},],
                        "conditions": [{"code": "1234",
                                        "text": "snomed diagnosis",
                                        "recordedDate": "1970-04-06T04:00:00Z"},
                                       {"code": "I10",
                                        "text": "Essential (primary) hypertension",
                                        "recordedDate": "1950-04-06T04:00:00Z"},
                                       ],
                    },
                    "dateOfClaim": "2021-11-09",
                    "diagnosticCode": "7101v2",
                    "disabilityActionType": "NEW",
                    "claimSubmissionId": "1234"
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
                                                            "value": 180}}],
                              "conditions": [{"code": "1234",
                                              'relevant': False,
                                              "text": "snomed diagnosis",
                                              "recordedDate": "1970-04-06T04:00:00Z"},
                                             {"code": "I10",
                                              "text": "Essential (primary) hypertension",
                                              'relevant': True,
                                              "recordedDate": "1950-04-06T04:00:00Z"}]},
                 "evidenceSummary": {"relevantConditionsCount": 1,
                                     "totalBpReadings": 1,
                                     "recentBpReadings": 1,
                                     "totalConditionsCount": 2},
                 "sufficientForFastTracking": False,
                 "claimSubmissionId": "1234"}

        ),
        # New claim with no condition and no recent BP, BP not elevated
        (
                {
                    "evidence": {
                        "bp_readings": [{"date": "2020-11-01",
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
                                                      "value": 100}},
                                        {"date": "2020-09-01",
                                         "diastolic": {"code": "8462-4",
                                                       "display": "Diastolic blood "
                                                                  "pressure",
                                                       "unit": "mm[Hg]",
                                                       "value": 90},
                                         "organization": "LYONS VA MEDICAL CENTER",
                                         "practitioner": "DR. JANE460 DOE922 MD",
                                         "systolic": {"code": "8480-6",
                                                      "display": "Systolic blood "
                                                                 "pressure",
                                                      "unit": "mm[Hg]",
                                                      "value": 200}}],
                        "conditions": [],
                    },
                    "dateOfClaim": "2021-11-09",
                    "diagnosticCode": "7101v2",
                    "disabilityActionType": "NEW",
                    "claimSubmissionId": "1234"
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
                                                            "value": 100}},
                                              {"date": "2020-09-01",
                                               "diastolic": {"code": "8462-4",
                                                             "display": "Diastolic blood "
                                                                        "pressure",
                                                             "unit": "mm[Hg]",
                                                             "value": 90},
                                               "organization": "LYONS VA MEDICAL CENTER",
                                               "practitioner": "DR. JANE460 DOE922 MD",
                                               "systolic": {"code": "8480-6",
                                                            "display": "Systolic blood "
                                                                       "pressure",
                                                            "unit": "mm[Hg]",
                                                            "value": 200}}],
                              "conditions": []},
                 "evidenceSummary": {"relevantConditionsCount": 0,
                                     "recentBpReadings": 2,
                                     "totalBpReadings": 2,
                                     "totalConditionsCount": 0},
                 "sufficientForFastTracking": None,
                 "claimSubmissionId": "1234"}
        ),
        # Claim for increase, not enough BP readings
        (
                {
                    "evidence": {
                        "bp_readings": [{"date": "2020-11-01",
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
                        "conditions": []
                    },
                    "dateOfClaim": "2021-11-09",
                    "diagnosticCode": "7101v2",
                    "disabilityActionType": "INCREASE",
                    "claimSubmissionId": "1234"
                }
                ,
                {"dateOfClaim": "2021-11-09",
                 "disabilityActionType": "INCREASE",
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
                 "evidenceSummary": {"relevantConditionsCount": 0,
                                     "recentBpReadings": 2,
                                     "totalBpReadings": 2,
                                     "totalConditionsCount": 0},
                 "sufficientForFastTracking": None,
                 "claimSubmissionId": "1234"}
        ),
        (
                {
                    "evidence": {
                        "bp_readings": [{"date": "2020-11-01",
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
                                                      "value": 200}},
                                        {"date": "2020-11-01",
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
                        "conditions": []
                    },
                    "dateOfClaim": "2021-11-09",
                    "diagnosticCode": "7101v2",
                    "disabilityActionType": "INCREASE",
                    "claimSubmissionId": "1234"
                }
                ,
                {"dateOfClaim": "2021-11-09",
                 "disabilityActionType": "INCREASE",
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
                                                            "value": 200}},
                                              {"date": "2020-11-01",
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
                 "evidenceSummary": {"relevantConditionsCount": 0,
                                     "recentBpReadings": 4,
                                     "totalBpReadings": 4,
                                     "totalConditionsCount": 0},
                 "sufficientForFastTracking": None,
                 "claimSubmissionId": "1234"}
        ),
        # Claim for increase
        (

                {
                    "evidence": {
                        "bp_readings": [{"date": "2021-08-01",
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
                                                      "value": 200}},
                                        {"date": "2021-11-01",
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
                                        {"date": "2021-10-01",
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
                        "medications": [],
                        "conditions": []
                    },
                    "dateOfClaim": "2021-11-09",
                    "diagnosticCode": "7101v2",
                    "disabilityActionType": "INCREASE",
                    "claimSubmissionId": "1234"
                }
                ,
                {"dateOfClaim": "2021-11-09",
                 "disabilityActionType": "INCREASE",
                 "evidence": {"bp_readings": [{"date": "2021-08-01",
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
                                                            "value": 200}},
                                              {"date": "2021-11-01",
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
                                              {"date": "2021-10-01",
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
                 "evidenceSummary": {"relevantConditionsCount": 0,
                                     "totalBpReadings": 4,
                                     "recentBpReadings": 4,
                                     "totalConditionsCount": 0},
                 "sufficientForFastTracking": True,
                 "claimSubmissionId": "1234"}
        ),
        (

                {
                    "evidence": {
                        "bp_readings": [],
                        "medications": [],
                        "conditions": []
                    },
                    "dateOfClaim": "2021-11-09",
                    "diagnosticCode": "7101v2",
                    "disabilityActionType": "INCREASE",
                    "claimSubmissionId": "1234"
                }
                ,
                {"dateOfClaim": "2021-11-09",
                 "disabilityActionType": "INCREASE",
                 "evidence": {"bp_readings": [], "conditions": []},
                 "evidenceSummary": {
                                     "relevantConditionsCount": 0,
                                     "totalBpReadings": 0,
                                     "recentBpReadings": 0,
                                     "totalConditionsCount": 0},
                 "sufficientForFastTracking": None,
                 "claimSubmissionId": "1234"}
        ),
        # Bad data missing action type
        (
                {
                    "evidence": {
                        "bp_readings": [],
                        "conditions": [],
                    },
                    "claimSubmissionId": "1234"
                },
                {"errorMessage": "error validating request message data",
                 "claimSubmissionId": "1234"}
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
                    "dateOfClaim": "2021-11-09",
                    "claimSubmissionId": "1234"
                },
                {"errorMessage": "error validating request message data",
                 "claimSubmissionId": "1234"}
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
    api_response = main.assess_sufficiency(request_body)

    assert api_response == response
