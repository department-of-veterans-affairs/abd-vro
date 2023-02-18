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
                            {'date': '2021-10-01',
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
                                          'value': 200.0}
                             },

                        ],
                        "conditions": [{"code": "I10",
                                        "text": "Essential (primary) hypertension",
                                        "recordedDate": "1950-04-06",
                                        "dataSource": "MAS"},
                                       {"code": "123",
                                        "text": "other condition",
                                        "recordedDate": "2020-04-06",
                                        "dataSource": "MAS"},
                                       {"code": "I10",
                                        "text": "Essential (primary) hypertension",
                                        "recordedDate": "",
                                        "category": "Encounter Diagnosis",
                                        "dataSource": "LH"}],
                        "procedures": [],
                        "medications": [],
                    },
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                    "disabilityActionType": "NEW",
                    "claimSubmissionId": "1234"
                }
                ,
                {'claimSubmissionDateTime': '2021-11-09T04:00:00Z',
                 'claimSubmissionId': '1234',
                 'disabilityActionType': 'NEW',
                 'evidence': {'bp_readings': [{'date': '2021-11-01',
                                               'dateFormatted': '11/1/2021',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 90},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 120}},
                                              {'date': '2021-10-01',
                                               'dateFormatted': '10/1/2021',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 110},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 200.0}},
                                              {'date': '2021-09-01',
                                               'dateFormatted': '9/1/2021',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 110},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 200.0}}],
                              'conditions': [{'code': '123',
                                              'dataSource': 'MAS',
                                              'dateFormatted': '4/6/2020',
                                              'document': '',
                                              'organization': '',
                                              'page': '',
                                              'partialDate': '',
                                              'receiptDate': '',
                                              'recordedDate': '2020-04-06',
                                              'relevant': False,
                                              'text': 'other condition'},
                                             {'code': 'I10',
                                              'dataSource': 'MAS',
                                              'dateFormatted': '4/6/1950',
                                              'document': '',
                                              'organization': '',
                                              'page': '',
                                              'partialDate': '',
                                              'receiptDate': '',
                                              'recordedDate': '1950-04-06',
                                              'relevant': True,
                                              'text': 'Essential (primary) hypertension'},
                                             {'category': 'Encounter Diagnosis',
                                              'code': 'I10',
                                              'dataSource': 'LH',
                                              'dateFormatted': '',
                                              'document': '',
                                              'organization': '',
                                              'page': '',
                                              'partialDate': '',
                                              'receiptDate': '',
                                              'recordedDate': '',
                                              'relevant': True,
                                              'text': 'Essential (primary) hypertension'}],
                              'medications': []},
                 'evidenceSummary': {'irrelevantConditionsCountMAS': 1,
                                     'medicationsCount': 0,
                                     'recentBpReadings': 3,
                                     'relevantConditionsCountLighthouse': 1,
                                     'relevantConditionsCountMAS': 1,
                                     'totalBpReadings': 3,
                                     'totalConditionsCount': 3},
                 'sufficientForFastTracking': True}
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
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                    "diagnosticCode": "7101",
                    "disabilityActionType": "NEW",
                    "claimSubmissionId": "1234"
                }
                ,
                {'claimSubmissionDateTime': '2021-11-09T04:00:00Z',
                 'claimSubmissionId': '1234',
                 'disabilityActionType': 'NEW',
                 'evidence': {'bp_readings': [{'date': '2020-11-01',
                                               'dateFormatted': '11/1/2020',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 115},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 180}},
                                              {'date': '2020-09-01',
                                               'dateFormatted': '9/1/2020',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 110},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 200}}],
                              'conditions': [],
                              'medications': []},
                 'evidenceSummary': {'irrelevantConditionsCountMAS': 0,
                                     'medicationsCount': 0,
                                     'recentBpReadings': 2,
                                     'relevantConditionsCountLighthouse': 0,
                                     'relevantConditionsCountMAS': 0,
                                     'totalBpReadings': 2,
                                     'totalConditionsCount': 0},
                 'sufficientForFastTracking': None}
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
                                                      "value": 180}}, ],
                        "conditions": [
                            {"code": "I10",
                             "text": "Essential (primary) hypertension",
                             "recordedDate": "1950-04-06",
                             "category": "Encounter Diagnosis",
                             "dataSource": "LH"},
                            {"code": "1234",
                             "text": "snomed diagnosis",
                             "recordedDate": "",
                             "category": "",
                             "dataSource": "LH"
                             },
                        ],
                        "medications": [{
                            "text": "some medication",
                            "relevant": True,
                            "authoredOn": "1950-04-06T07:24:55Z",
                        },
                            {
                                "text": "some medication",
                                "relevant": True,
                                "authoredOn": "",
                                "partialDate": "**/**/1988"
                            }]
                    },
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                    "diagnosticCode": "7101",
                    "disabilityActionType": "NEW",
                    "claimSubmissionId": "1234"
                }
                ,
                {'claimSubmissionDateTime': '2021-11-09T04:00:00Z',
                 'claimSubmissionId': '1234',
                 'disabilityActionType': 'NEW',
                 'evidence': {'bp_readings': [{'date': '2020-11-01',
                                               'dateFormatted': '11/1/2020',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 115},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 180}}],
                              'conditions': [{'category': 'Encounter Diagnosis',
                                              'code': 'I10',
                                              'dataSource': 'LH',
                                              'dateFormatted': '4/6/1950',
                                              'document': '',
                                              'organization': '',
                                              'page': '',
                                              'partialDate': '',
                                              'receiptDate': '',
                                              'recordedDate': '1950-04-06',
                                              'relevant': True,
                                              'text': 'Essential (primary) hypertension'},
                                             {'category': '',
                                              'code': '1234',
                                              'dataSource': 'LH',
                                              'dateFormatted': '',
                                              'document': '',
                                              'organization': '',
                                              'page': '',
                                              'partialDate': '',
                                              'receiptDate': '',
                                              'recordedDate': '',
                                              'relevant': False,
                                              'text': 'snomed diagnosis'}],
                              'medications': [{'authoredOn': '1950-04-06T07:24:55Z',
                                               'dateFormatted': '4/6/1950',
                                               'document': '',
                                               'organization': '',
                                               'page': '',
                                               'partialDate': '',
                                               'receiptDate': '',
                                               'relevant': True,
                                               'text': 'some medication'},
                                              {'authoredOn': '',
                                               'dateFormatted': '',
                                               'document': '',
                                               'organization': '',
                                               'page': '',
                                               'partialDate': '**/**/1988',
                                               'receiptDate': '',
                                               'relevant': True,
                                               'text': 'some medication'}]},
                 'evidenceSummary': {'irrelevantConditionsCountMAS': 0,
                                     'medicationsCount': 2,
                                     'recentBpReadings': 1,
                                     'relevantConditionsCountLighthouse': 1,
                                     'relevantConditionsCountMAS': 0,
                                     'totalBpReadings': 1,
                                     'totalConditionsCount': 2},
                 'sufficientForFastTracking': False}
        ),
        # New claim with no condition and no recent BP, BP not elevated
        (
                {
                    "evidence": {
                        "bp_readings": [{"date": "", # missing date
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
                                        {'date': '2020-11-01',
                                         'dateFormatted': '9/1/2020',
                                         'diastolic': {'code': '8462-4',
                                                       'display': 'Diastolic blood '
                                                                  'pressure',
                                                       'unit': 'mm[Hg]',
                                                       'value': 90},
                                         'organization': 'LYONS VA MEDICAL CENTER',
                                         'practitioner': 'DR. JANE460 DOE922 MD',
                                         'systolic': {'code': '8480-6',
                                                      'display': 'Systolic blood '
                                                                 'pressure',
                                                      'unit': 'mm[Hg]',
                                                      'value': 200}},
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
                        "medications": [{
                            "text": "some medication",
                            "relevant": True
                        }]
                    },
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                    "diagnosticCode": "7101",
                    "disabilityActionType": "NEW",
                    "claimSubmissionId": "1234"
                }
                ,
                {'claimSubmissionDateTime': '2021-11-09T04:00:00Z',
                 'claimSubmissionId': '1234',
                 'disabilityActionType': 'NEW',
                 'evidence': {'bp_readings': [{'date': '2020-11-01',
                                               'dateFormatted': '11/1/2020',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 90},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 200}},
                                              {'date': '2020-09-01',
                                               'dateFormatted': '9/1/2020',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 90},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 200}},
                                              {'date': '',
                                               'dateFormatted': '',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 115},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 100}}],
                              'conditions': [],
                              'medications': [{'dateFormatted': '',
                                               'document': '',
                                               'organization': '',
                                               'page': '',
                                               'partialDate': '',
                                               'receiptDate': '',
                                               'relevant': True,
                                               'text': 'some medication'}]},
                 'evidenceSummary': {'irrelevantConditionsCountMAS': 0,
                                     'medicationsCount': 1,
                                     'recentBpReadings': 2,
                                     'relevantConditionsCountLighthouse': 0,
                                     'relevantConditionsCountMAS': 0,
                                     'totalBpReadings': 3,
                                     'totalConditionsCount': 0},
                 'sufficientForFastTracking': None}
        ),
        # Claim for increase, not enough BP readings
        (
                {
                    "evidence": {
                        "bp_readings": [{"date": "****-11-01",  # Date is not formatted correctly
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
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                    "diagnosticCode": "7101",
                    "disabilityActionType": "INCREASE",
                    "claimSubmissionId": "1234"
                }
                ,
                {'claimSubmissionDateTime': '2021-11-09T04:00:00Z',
                 'claimSubmissionId': '1234',
                 'disabilityActionType': 'INCREASE',
                 'evidence': {'bp_readings': [{'date': '2020-09-01',
                                               'dateFormatted': '9/1/2020',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 110},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 200}}],
                              'conditions': []},
                 'evidenceSummary': {'irrelevantConditionsCountMAS': 0,
                                     'recentBpReadings': 1,
                                     'relevantConditionsCountLighthouse': 0,
                                     'relevantConditionsCountMAS': 0,
                                     'totalBpReadings': 2,
                                     'totalConditionsCount': 0},
                 'sufficientForFastTracking': None}
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
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                    "diagnosticCode": "7101",
                    "disabilityActionType": "INCREASE",
                    "claimSubmissionId": "1234"
                }
                ,
                {'claimSubmissionDateTime': '2021-11-09T04:00:00Z',
                 'claimSubmissionId': '1234',
                 'disabilityActionType': 'INCREASE',
                 'evidence': {'bp_readings': [{'date': '2020-11-01',
                                               'dateFormatted': '11/1/2020',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 115},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 180}},
                                              {'date': '2020-11-01',
                                               'dateFormatted': '11/1/2020',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 115},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 180}},
                                              {'date': '2020-09-01',
                                               'dateFormatted': '9/1/2020',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 110},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 200}},
                                              {'date': '2020-09-01',
                                               'dateFormatted': '9/1/2020',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 110},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 200}}],
                              'conditions': []},
                 'evidenceSummary': {'irrelevantConditionsCountMAS': 0,
                                     'recentBpReadings': 4,
                                     'relevantConditionsCountLighthouse': 0,
                                     'relevantConditionsCountMAS': 0,
                                     'totalBpReadings': 4,
                                     'totalConditionsCount': 0},
                 'sufficientForFastTracking': None}

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
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                    "diagnosticCode": "7101",
                    "disabilityActionType": "INCREASE",
                    "claimSubmissionId": "1234"
                }
                ,
                {'claimSubmissionDateTime': '2021-11-09T04:00:00Z',
                 'claimSubmissionId': '1234',
                 'disabilityActionType': 'INCREASE',
                 'evidence': {'bp_readings': [{'date': '2021-11-01',
                                               'dateFormatted': '11/1/2021',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 115},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 180}},
                                              {'date': '2021-10-01',
                                               'dateFormatted': '10/1/2021',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 110},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 200}},
                                              {'date': '2021-09-01',
                                               'dateFormatted': '9/1/2021',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 110},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 200}},
                                              {'date': '2021-08-01',
                                               'dateFormatted': '8/1/2021',
                                               'diastolic': {'code': '8462-4',
                                                             'display': 'Diastolic blood '
                                                                        'pressure',
                                                             'unit': 'mm[Hg]',
                                                             'value': 115},
                                               'document': '',
                                               'organization': 'LYONS VA MEDICAL CENTER',
                                               'page': '',
                                               'partialDate': '',
                                               'practitioner': 'DR. JANE460 DOE922 MD',
                                               'receiptDate': '',
                                               'systolic': {'code': '8480-6',
                                                            'display': 'Systolic blood '
                                                                       'pressure',
                                                            'unit': 'mm[Hg]',
                                                            'value': 180}}],
                              'conditions': [],
                              'medications': []},
                 'evidenceSummary': {'irrelevantConditionsCountMAS': 0,
                                     'medicationsCount': 0,
                                     'recentBpReadings': 4,
                                     'relevantConditionsCountLighthouse': 0,
                                     'relevantConditionsCountMAS': 0,
                                     'totalBpReadings': 4,
                                     'totalConditionsCount': 0},
                 'sufficientForFastTracking': True}

        ),
        (

                {
                    "evidence": {
                        "bp_readings": [],
                        "medications": [],
                        "conditions": []
                    },
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                    "diagnosticCode": "7101",
                    "disabilityActionType": "INCREASE",
                    "claimSubmissionId": "1234"
                }
                ,
                {'claimSubmissionDateTime': '2021-11-09T04:00:00Z',
                 'claimSubmissionId': '1234',
                 'disabilityActionType': 'INCREASE',
                 'evidence': {'bp_readings': [], 'conditions': [], 'medications': []},
                 'evidenceSummary': {'irrelevantConditionsCountMAS': 0,
                                     'medicationsCount': 0,
                                     'recentBpReadings': 0,
                                     'relevantConditionsCountLighthouse': 0,
                                     'relevantConditionsCountMAS': 0,
                                     'totalBpReadings': 0,
                                     'totalConditionsCount': 0},
                 'sufficientForFastTracking': None}
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
                        "medications": []
                    },
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
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
