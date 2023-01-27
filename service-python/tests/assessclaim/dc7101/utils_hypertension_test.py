import pytest

from assessclaimdc7101.src.lib import utils


@pytest.mark.parametrize(
    "request_body, result_is_valid, errors",
    [
        (
                {
                    "evidence":
                        {
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
                            ],
                            "medications": [{"description": "Capoten",
                                             "status": "active",
                                             "authoredOn": "1950-04-06T04:00:00Z",
                                             "asthmaRelevant": "false"}],
                            "conditions": []
                        },
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                    "diagnosticCode": "7101",
                    "veteranIcn": "1234567890V123456",
                    "disabilityActionType": "NEW"
                },
                True,
                {}
        ),
        (
                {
                    "evidence": {

                        "bp_readings": [
                            {
                                "systolic": {
                                    "code": "8480-6",
                                    "display": "Systolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": "180"
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
                                    "value": "115"
                                },
                                "systolic": {
                                    "code": "8480-6",
                                    "display": "Systolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 180
                                },
                                "date": 20211101,
                                "practitioner": "DR. JANE460 DOE922 MD",
                                "organization": "LYONS VA MEDICAL CENTER"
                            },
                        ],
                        "medications": [{"description": 123}],
                        "conditions": []
                    },
                    "claimSubmissionDateTime": 20211109,
                    "diagnosticCode": "7101",
                    "disabilityActionType": "NEW"
                },
                False,
                {'claimSubmissionDateTime': ['must be of string type'],
                 'evidence': [{'bp_readings': [{0: [{'diastolic': ['required field'],
                                                     'systolic': [{'value': ['must be of '
                                                                             'number type']}]}],
                                                1: [{'date': ['must be of string type'],
                                                     'diastolic': [{'value': ['must be of '
                                                                              'number '
                                                                              'type']}]}]}],
                               'medications': [{0: [{'description': ['must be of string '
                                                                     'type']}]}]}]}
        ),
    ],
)
def test_validate_request_body(request_body, result_is_valid, errors):
    """
    Test function that determines if the blood pressure readings contain a readings that are within 1 month and 6 months of the date of claim

    :param claimSubmissionDateTime: string representation of the date of claim
    :type claimSubmissionDateTime: string
    :param bp_readings: list of blood pressure readings
    :type bp_readings: list
    :param result: boolean describing whether or not the blood pressure readings meet the specifications
    :type result: bool
    """
    result = utils.validate_request_body(request_body)
    assert result["is_valid"] == result_is_valid
    assert result["errors"] == errors
