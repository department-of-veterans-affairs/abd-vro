import pytest

from assessclaimdc7101.src.lib import main


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
                                "value": 115,
                            },
                            "systolic": {
                                "code": "8480-6",
                                "display": "Systolic blood pressure",
                                "unit": "mm[Hg]",
                                "value": 180,
                            },
                            "dataSource": "",
                            "date": "2021-11-01",
                            "practitioner": "DR. JANE460 DOE922 MD",
                            "organization": "LYONS VA MEDICAL CENTER",
                        },
                        {
                            "diastolic": {
                                "code": "8462-4",
                                "display": "Diastolic blood pressure",
                                "unit": "mm[Hg]",
                                "value": 110,
                            },
                            "systolic": {
                                "code": "8480-6",
                                "display": "Systolic blood pressure",
                                "unit": "mm[Hg]",
                                "value": 200.0,
                            },
                            "dataSource": "",
                            "date": "2021-09-01",
                            "practitioner": "DR. JANE460 DOE922 MD",
                            "organization": "LYONS VA MEDICAL CENTER",
                        },
                    ],
                    "medications": [
                        {
                            "description": "Capoten",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        }
                    ],
                },
                "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                "claimSubmissionId": "1234"
            },
            {'evidence': {'bp_readings': [{'date': '2021-11-01',
                                           'receiptDate': '',
                                           "dataSource": "",
                                           'dateFormatted': '11/1/2021',
                                           'diastolic': {'code': '8462-4',
                                                         'display': 'Diastolic blood '
                                                                    'pressure',
                                                         'unit': 'mm[Hg]',
                                                         'value': 115},
                                           'organization': 'LYONS VA MEDICAL CENTER',
                                           'practitioner': 'DR. JANE460 DOE922 MD',
                                           'systolic': {'code': '8480-6',
                                                        'display': 'Systolic blood '
                                                                   'pressure',
                                                        'unit': 'mm[Hg]',
                                                        'value': 180}},
                                          {'date': '2021-09-01',
                                           'receiptDate': '',
                                           "dataSource": "",
                                           'dateFormatted': '9/1/2021',
                                           'diastolic': {'code': '8462-4',
                                                         'display': 'Diastolic blood '
                                                                    'pressure',
                                                         'unit': 'mm[Hg]',
                                                         'value': 110},
                                           'organization': 'LYONS VA MEDICAL CENTER',
                                           'practitioner': 'DR. JANE460 DOE922 MD',
                                           'systolic': {'code': '8480-6',
                                                        'display': 'Systolic blood '
                                                                   'pressure',
                                                        'unit': 'mm[Hg]',
                                                        'value': 200.0}}],
                          'medications': [{'authoredOn': '1950-04-06T04:00:00Z',
                                           'description': 'Capoten',
                                           'status': 'active'}]},
             'evidenceSummary': {'medicationsCount': 1,
                                 'recentBpCount': 2,
                                 'totalBpCount': 2},
                "claimSubmissionId": "1234"
            },
        ),
        (
            {
                "evidence": {
                    "bp_readings": [
                        {
                            "diastolic": {
                                "code": "8462-4",
                                "display": "Diastolic blood pressure",
                                "unit": "mm[Hg]",
                                "value": 115,
                            },
                            "systolic": {
                                "code": "8480-6",
                                "display": "Systolic blood pressure",
                                "unit": "mm[Hg]",
                                "value": 180,
                            },
                            "date": "2020-11-01",
                            "dataSource": "",
                            "practitioner": "DR. JANE460 DOE922 MD",
                            "organization": "LYONS VA MEDICAL CENTER",
                        },
                        {
                            "diastolic": {
                                "code": "8462-4",
                                "display": "Diastolic blood pressure",
                                "unit": "mm[Hg]",
                                "value": 110,
                            },
                            "systolic": {
                                "code": "8480-6",
                                "display": "Systolic blood pressure",
                                "unit": "mm[Hg]",
                                "value": 200,
                            },
                            "date": "2020-09-01",
                            "dataSource": "",
                            "practitioner": "DR. JANE460 DOE922 MD",
                            "organization": "LYONS VA MEDICAL CENTER",
                        },
                    ],
                    "medications": [],
                },
                "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                "diagnosticCode": "7101",
                "claimSubmissionId": "1234"
            },
            # Blood pressue readings don't meet date specs
            {
                "evidence": {"bp_readings": [], "medications": []},
                "evidenceSummary": {
                    "medicationsCount": 0,
                    "recentBpCount": 0,
                    "totalBpCount": 2,
                },
                "claimSubmissionId": "1234"
            },
        ),
        # Sufficiency and history algos fail
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "medications": [],
                },
                "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                "diagnosticCode": "7101",
                "claimSubmissionId": "1234"
            },
            {
                "evidence": {"bp_readings": [], "medications": []},
                "evidenceSummary": {
                    "medicationsCount": 0,
                    "recentBpCount": 0,
                    "totalBpCount": 0,
                },
                "claimSubmissionId": "1234"
            },
        ),
        # Un-readable date
        (
                {
                    "evidence": {
                        "bp_readings": [],
                        "medications": [],
                    },
                    "claimSubmissionDateTime": "2021-11-09T04",
                    "diagnosticCode": "7101",
                    "claimSubmissionId": "1234"
                },
                {
                    "evidence": {"bp_readings": [], "medications": []},
                    "evidenceSummary": {
                        "medicationsCount": 0,
                        "recentBpCount": 0,
                        "totalBpCount": 0,
                    },
                    "claimSubmissionId": "1234"
                }
        )
        ,
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
                                "value": "115",
                            },
                            "systolic": {
                                "code": "8480-6",
                                "display": "Systolic blood pressure",
                                "unit": "mm[Hg]",
                                "value": 180,
                            },
                            "date": "2021-11-01",
                            'receiptDate': '',
                            "practitioner": "DR. JANE460 DOE922 MD",
                            "organization": "LYONS VA MEDICAL CENTER",
                        },
                        {
                            "diastolic": {
                                "code": "8462-4",
                                "display": "Diastolic blood pressure",
                                "unit": "mm[Hg]",
                                "value": 110,
                            },
                            "systolic": {
                                "code": "8480-6",
                                "display": "Systolic blood pressure",
                                "unit": "mm[Hg]",
                                "value": 200,
                            },
                            "date": "2021-09-01",
                            'receiptDate': '',
                            "practitioner": "DR. JANE460 DOE922 MD",
                            "organization": "LYONS VA MEDICAL CENTER",
                        },
                    ],
                    "medications": [{"description": 11}],
                },
                "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                "claimSubmissionId": "1234"
            },
            {
                "errorMessage": "error validating request message data",
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
    api_response = main.assess_hypertension(request_body)

    assert api_response == response
