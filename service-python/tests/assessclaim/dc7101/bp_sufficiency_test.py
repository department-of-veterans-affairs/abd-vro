import pytest

from assessclaimdc7101.src.lib import bp_calculator


@pytest.mark.parametrize(
    "request_body, bp_calculator_result",
    [
        # Two readings. No out of range dates.
        (
                {"evidence": {
                    "bp_readings": [
                        {
                            "diastolic": {
                                "value": 115
                            },
                            "systolic": {
                                "value": 180
                            },
                            "receiptDate": "",
                            "date": "2021-11-01"
                        },
                        {
                            "diastolic": {
                                "value": 110
                            },
                            "systolic": {
                                "value": 200
                            },
                            "receiptDate": "",
                            "date": "2021-09-01"
                        }
                    ]
                }
                    ,
                    "claimSubmissionDateTime": "2021-11-09T17:45:59Z",
                },
                {'allBp': [{'date': '2021-11-01',
                            "receiptDate": "",
                            'dateFormatted': '11/1/2021',
                            'diastolic': {'value': 115},
                            'systolic': {'value': 180}},
                           {'date': '2021-09-01',
                            "receiptDate": "",
                            'dateFormatted': '9/1/2021',
                            'diastolic': {'value': 110},
                            'systolic': {'value': 200}}],
                 'oneYearBp': [{'date': '2021-11-01',
                                "receiptDate": "",
                                'dateFormatted': '11/1/2021',
                                'diastolic': {'value': 115},
                                'systolic': {'value': 180}},
                               {'date': '2021-09-01',
                                "receiptDate": "",
                                'dateFormatted': '9/1/2021',
                                'diastolic': {'value': 110},
                                'systolic': {'value': 200}}],
                 'oneYearBpReadings': 2,
                 'recentElevatedBpReadings': 2,
                 'totalBpReadings': 2,
                 'twoYearsBp': [{'date': '2021-11-01',
                                 "receiptDate": "",
                                 'dateFormatted': '11/1/2021',
                                 'diastolic': {'value': 115},
                                 'systolic': {'value': 180}},
                                {'date': '2021-09-01',
                                 "receiptDate": "",
                                 'dateFormatted': '9/1/2021',
                                 'diastolic': {'value': 110},
                                 'systolic': {'value': 200}}],
                 'twoYearsBpReadings': 2}
        ),
        # 3 reading test case with one out of range date
        (

                {"evidence": {
                    "bp_readings": [
                        {
                            "diastolic": {
                                "value": 115
                            },
                            "systolic": {
                                "value": 180
                            },
                            "receiptDate": "",
                            "date": "2019-11-01"
                        },
                        {
                            "diastolic": {
                                "value": 110
                            },
                            "systolic": {
                                "value": 200
                            },
                            "receiptDate": "",
                            "date": "2021-09-01"
                        },
                        {
                            "diastolic": {
                                "value": 120
                            },
                            "systolic": {
                                "value": 210
                            },
                            "receiptDate": "",
                            "date": "2020-11-09"
                        }
                    ]
                }
                    ,
                    "claimSubmissionDateTime": "2021-11-09T17:45:59Z",
                },
                {'allBp': [{'date': '2021-09-01',
                            "receiptDate": "",
                            'dateFormatted': '9/1/2021',
                            'diastolic': {'value': 110},
                            'systolic': {'value': 200}},
                           {'date': '2020-11-09',
                            "receiptDate": "",
                            'dateFormatted': '11/9/2020',
                            'diastolic': {'value': 120},
                            'systolic': {'value': 210}},
                           {'date': '2019-11-01',
                            "receiptDate": "",
                            'dateFormatted': '11/1/2019',
                            'diastolic': {'value': 115},
                            'systolic': {'value': 180}}],
                 'oneYearBp': [{'date': '2021-09-01',
                                "receiptDate": "",
                                'dateFormatted': '9/1/2021',
                                'diastolic': {'value': 110},
                                'systolic': {'value': 200}},
                               {'date': '2020-11-09',
                                "receiptDate": "",
                                'dateFormatted': '11/9/2020',
                                'diastolic': {'value': 120},
                                'systolic': {'value': 210}}],
                 'oneYearBpReadings': 2,
                 'recentElevatedBpReadings': 2,
                 'totalBpReadings': 3,
                 'twoYearsBp': [{'date': '2021-09-01',
                                 "receiptDate": "",
                                 'dateFormatted': '9/1/2021',
                                 'diastolic': {'value': 110},
                                 'systolic': {'value': 200}},
                                {'date': '2020-11-09',
                                 "receiptDate": "",
                                 'dateFormatted': '11/9/2020',
                                 'diastolic': {'value': 120},
                                 'systolic': {'value': 210}}],
                 'twoYearsBpReadings': 2}
        ),
        (
                {
                    "evidence": {

                        "bp_readings": [
                            {
                                "diastolic": {
                                    "value": 112
                                },
                                "systolic": {
                                    "value": 181
                                },
                                "receiptDate": "",
                                "date": "2021-10-09"
                            },
                            {
                                "diastolic": {
                                    "value": 109
                                },
                                "systolic": {
                                    "value": 181
                                },
                                "receiptDate": "",
                                "date": "2021-10-10"
                            },
                            {
                                "diastolic": {
                                    "value": 113
                                },
                                "systolic": {
                                    "value": 131
                                },
                                "receiptDate": "",
                                "date": "2021-05-13"
                            },
                            {
                                "diastolic": {
                                    "value": 101
                                },
                                "systolic": {
                                    "value": 160
                                },
                                "receiptDate": "",
                                "date": "2021-09-13"
                            },
                            {
                                "diastolic": {
                                    "value": 104
                                },
                                "systolic": {
                                    "value": 120
                                },
                                "receiptDate": "",
                                "date": "2021-09-13"
                            },
                            {
                                "diastolic": {
                                    "value": 116
                                },
                                "systolic": {
                                    "value": 180
                                },
                                "receiptDate": "",
                                "date": "2021-10-13"
                            },
                            {
                                "diastolic": {
                                    "value": 111
                                },
                                "systolic": {
                                    "value": 155
                                },
                                "receiptDate": "",
                                "date": "2021-10-14"
                            }
                        ]
                    }
                    ,
                    "claimSubmissionDateTime": "2021-11-09T17:45:59Z",
                },
                {'allBp': [{'date': '2021-10-14',
                            "receiptDate": "",
                            'dateFormatted': '10/14/2021',
                            'diastolic': {'value': 111},
                            'systolic': {'value': 155}},
                           {'date': '2021-10-13',
                            "receiptDate": "",
                            'dateFormatted': '10/13/2021',
                            'diastolic': {'value': 116},
                            'systolic': {'value': 180}},
                           {'date': '2021-10-10',
                            "receiptDate": "",
                            'dateFormatted': '10/10/2021',
                            'diastolic': {'value': 109},
                            'systolic': {'value': 181}},
                           {'date': '2021-10-09',
                            "receiptDate": "",
                            'dateFormatted': '10/9/2021',
                            'diastolic': {'value': 112},
                            'systolic': {'value': 181}},
                           {'date': '2021-09-13',
                            "receiptDate": "",
                            'dateFormatted': '9/13/2021',
                            'diastolic': {'value': 101},
                            'systolic': {'value': 160}},
                           {'date': '2021-09-13',
                            "receiptDate": "",
                            'dateFormatted': '9/13/2021',
                            'diastolic': {'value': 104},
                            'systolic': {'value': 120}},
                           {'date': '2021-05-13',
                            "receiptDate": "",
                            'dateFormatted': '5/13/2021',
                            'diastolic': {'value': 113},
                            'systolic': {'value': 131}}],
                 'oneYearBp': [{'date': '2021-10-14',
                                "receiptDate": "",
                                'dateFormatted': '10/14/2021',
                                'diastolic': {'value': 111},
                                'systolic': {'value': 155}},
                               {'date': '2021-10-13',
                                "receiptDate": "",
                                'dateFormatted': '10/13/2021',
                                'diastolic': {'value': 116},
                                'systolic': {'value': 180}},
                               {'date': '2021-10-10',
                                "receiptDate": "",
                                'dateFormatted': '10/10/2021',
                                'diastolic': {'value': 109},
                                'systolic': {'value': 181}},
                               {'date': '2021-10-09',
                                "receiptDate": "",
                                'dateFormatted': '10/9/2021',
                                'diastolic': {'value': 112},
                                'systolic': {'value': 181}},
                               {'date': '2021-09-13',
                                "receiptDate": "",
                                'dateFormatted': '9/13/2021',
                                'diastolic': {'value': 101},
                                'systolic': {'value': 160}},
                               {'date': '2021-09-13',
                                "receiptDate": "",
                                'dateFormatted': '9/13/2021',
                                'diastolic': {'value': 104},
                                'systolic': {'value': 120}},
                               {'date': '2021-05-13',
                                "receiptDate": "",
                                'dateFormatted': '5/13/2021',
                                'diastolic': {'value': 113},
                                'systolic': {'value': 131}}],
                 'oneYearBpReadings': 7,
                 'recentElevatedBpReadings': 4,
                 'totalBpReadings': 7,
                 'twoYearsBp': [{'date': '2021-10-14',
                                 "receiptDate": "",
                                 'dateFormatted': '10/14/2021',
                                 'diastolic': {'value': 111},
                                 'systolic': {'value': 155}},
                                {'date': '2021-10-13',
                                 "receiptDate": "",
                                 'dateFormatted': '10/13/2021',
                                 'diastolic': {'value': 116},
                                 'systolic': {'value': 180}},
                                {'date': '2021-10-10',
                                 "receiptDate": "",
                                 'dateFormatted': '10/10/2021',
                                 'diastolic': {'value': 109},
                                 'systolic': {'value': 181}},
                                {'date': '2021-10-09',
                                 "receiptDate": "",
                                 'dateFormatted': '10/9/2021',
                                 'diastolic': {'value': 112},
                                 'systolic': {'value': 181}},
                                {'date': '2021-09-13',
                                 "receiptDate": "",
                                 'dateFormatted': '9/13/2021',
                                 'diastolic': {'value': 101},
                                 'systolic': {'value': 160}},
                                {'date': '2021-09-13',
                                 "receiptDate": "",
                                 'dateFormatted': '9/13/2021',
                                 'diastolic': {'value': 104},
                                 'systolic': {'value': 120}},
                                {'date': '2021-05-13',
                                 "receiptDate": "",
                                 'dateFormatted': '5/13/2021',
                                 'diastolic': {'value': 113},
                                 'systolic': {'value': 131}}],
                 'twoYearsBpReadings': 7}
        ),
        (
                {
                    "evidence": {

                        "bp_readings": [
                            {
                                "diastolic": {
                                    "value": 109
                                },
                                "systolic": {
                                    "value": 181
                                },
                                "receiptDate": "",
                                "date": "2021-10-10"
                            },
                            {
                                "diastolic": {
                                    "value": 113
                                },
                                "systolic": {
                                    "value": 131
                                },
                                "receiptDate": "",
                                "date": ""  # no date
                            },
                            {
                                "diastolic": {
                                    "value": 101
                                },
                                "systolic": {
                                    "value": 160
                                },
                                "receiptDate": "",
                                "date": "2021-09-13"
                            },
                            {
                                "diastolic": {
                                    "value": 104
                                },
                                "systolic": {
                                    "value": 120
                                },
                                "receiptDate": "",
                                "date": "2021-09-13"
                            },
                            {
                                "diastolic": {
                                    "value": 116
                                },
                                "systolic": {
                                    "value": 180
                                },
                                "receiptDate": "",
                                "date": "2021-10-13"
                            },
                            {
                                "diastolic": {
                                    "value": 111
                                },
                                "systolic": {
                                    "value": 155
                                },
                                "receiptDate": "",
                                "date": "2021-10-14"
                            }
                        ]
                    }
                    ,
                    "claimSubmissionDateTime": "2021-11-09T17:45:59Z",
                },
                {'allBp': [{'date': '2021-10-14',
                            "receiptDate": "",
                            'dateFormatted': '10/14/2021',
                            'diastolic': {'value': 111},
                            'systolic': {'value': 155}},
                           {'date': '2021-10-13',
                            "receiptDate": "",
                            'dateFormatted': '10/13/2021',
                            'diastolic': {'value': 116},
                            'systolic': {'value': 180}},
                           {'date': '2021-10-10',
                            "receiptDate": "",
                            'dateFormatted': '10/10/2021',
                            'diastolic': {'value': 109},
                            'systolic': {'value': 181}},
                           {'date': '2021-09-13',
                            "receiptDate": "",
                            'dateFormatted': '9/13/2021',
                            'diastolic': {'value': 101},
                            'systolic': {'value': 160}},
                           {'date': '2021-09-13',
                            "receiptDate": "",
                            'dateFormatted': '9/13/2021',
                            'diastolic': {'value': 104},
                            'systolic': {'value': 120}},
                           {'date': '',
                            "receiptDate": "",
                            'dateFormatted': '',
                            'diastolic': {'value': 113},
                            'systolic': {'value': 131}}],
                 'oneYearBp': [{'date': '2021-10-14',
                                "receiptDate": "",
                                'dateFormatted': '10/14/2021',
                                'diastolic': {'value': 111},
                                'systolic': {'value': 155}},
                               {'date': '2021-10-13',
                                "receiptDate": "",
                                'dateFormatted': '10/13/2021',
                                'diastolic': {'value': 116},
                                'systolic': {'value': 180}},
                               {'date': '2021-10-10',
                                "receiptDate": "",
                                'dateFormatted': '10/10/2021',
                                'diastolic': {'value': 109},
                                'systolic': {'value': 181}},
                               {'date': '2021-09-13',
                                "receiptDate": "",
                                'dateFormatted': '9/13/2021',
                                'diastolic': {'value': 101},
                                'systolic': {'value': 160}},
                               {'date': '2021-09-13',
                                "receiptDate": "",
                                'dateFormatted': '9/13/2021',
                                'diastolic': {'value': 104},
                                'systolic': {'value': 120}},
                               ],
                 'oneYearBpReadings': 5,
                 'recentElevatedBpReadings': 3,
                 'totalBpReadings': 6,
                 'twoYearsBp': [{'date': '2021-10-14',
                                 "receiptDate": "",
                                 'dateFormatted': '10/14/2021',
                                 'diastolic': {'value': 111},
                                 'systolic': {'value': 155}},
                                {'date': '2021-10-13',
                                 "receiptDate": "",
                                 'dateFormatted': '10/13/2021',
                                 'diastolic': {'value': 116},
                                 'systolic': {'value': 180}},
                                {'date': '2021-10-10',
                                 "receiptDate": "",
                                 'dateFormatted': '10/10/2021',
                                 'diastolic': {'value': 109},
                                 'systolic': {'value': 181}},
                                {'date': '2021-09-13',
                                 "receiptDate": "",
                                 'dateFormatted': '9/13/2021',
                                 'diastolic': {'value': 101},
                                 'systolic': {'value': 160}},
                                {'date': '2021-09-13',
                                 "receiptDate": "",
                                 'dateFormatted': '9/13/2021',
                                 'diastolic': {'value': 104},
                                 'systolic': {'value': 120}},
                                ],
                 'twoYearsBpReadings': 5}
        ),
        (
                {
                    "evidence": {

                        "bp_readings": [
                            {
                                "diastolic": {
                                    "value": 109
                                },
                                "systolic": {
                                    "value": 181
                                },
                                "receiptDate": "",
                                "date": "2021-10-10"
                            },
                            {
                                "diastolic": {
                                    "value": 113
                                },
                                "systolic": {
                                    "value": 131
                                },
                                "receiptDate": "",
                                "date": "2021-05-13"
                            },
                            {
                                "diastolic": {
                                    "value": 101
                                },
                                "systolic": {
                                    "value": 160
                                },
                                "receiptDate": "",
                                "date": "2021-09-13"
                            },
                            {
                                "diastolic": {
                                    "value": 104
                                },
                                "systolic": {
                                    "value": 120
                                },
                                "receiptDate": "",
                                "date": "2021-09-13"
                            },
                            {
                                "diastolic": {
                                    "value": 116
                                },
                                "systolic": {
                                    "value": 180
                                },
                                "date": "2021-10-13",
                                "receiptDate": "",
                            },
                            {
                                "diastolic": {
                                    "value": 111
                                },
                                "systolic": {
                                    "value": 155
                                },
                                "date": "2021-10-14",
                                "receiptDate": "",
                            },
                            {
                                "diastolic": {
                                    "value": 105
                                },
                                "systolic": {
                                    "value": 154
                                },
                                "date": "2020-11-08",
                                "receiptDate": "",
                            }
                        ]
                    }
                    ,
                    "claimSubmissionDateTime": "2021-11-09T17:45:59Z",
                },
                {'allBp': [{'date': '2021-10-14',
                            "receiptDate": "",
                            'dateFormatted': '10/14/2021',
                            'diastolic': {'value': 111},
                            'systolic': {'value': 155}},
                           {'date': '2021-10-13',
                            "receiptDate": "",
                            'dateFormatted': '10/13/2021',
                            'diastolic': {'value': 116},
                            'systolic': {'value': 180}},
                           {'date': '2021-10-10',
                            "receiptDate": "",
                            'dateFormatted': '10/10/2021',
                            'diastolic': {'value': 109},
                            'systolic': {'value': 181}},
                           {'date': '2021-09-13',
                            "receiptDate": "",
                            'dateFormatted': '9/13/2021',
                            'diastolic': {'value': 101},
                            'systolic': {'value': 160}},
                           {'date': '2021-09-13',
                            "receiptDate": "",
                            'dateFormatted': '9/13/2021',
                            'diastolic': {'value': 104},
                            'systolic': {'value': 120}},
                           {'date': '2021-05-13',
                            "receiptDate": "",
                            'dateFormatted': '5/13/2021',
                            'diastolic': {'value': 113},
                            'systolic': {'value': 131}},
                           {'date': '2020-11-08',
                            "receiptDate": "",
                            'dateFormatted': '11/8/2020',
                            'diastolic': {'value': 105},
                            'systolic': {'value': 154}}],
                 'oneYearBp': [{'date': '2021-10-14',
                                "receiptDate": "",
                                'dateFormatted': '10/14/2021',
                                'diastolic': {'value': 111},
                                'systolic': {'value': 155}},
                               {'date': '2021-10-13',
                                "receiptDate": "",
                                'dateFormatted': '10/13/2021',
                                'diastolic': {'value': 116},
                                'systolic': {'value': 180}},
                               {'date': '2021-10-10',
                                "receiptDate": "",
                                'dateFormatted': '10/10/2021',
                                'diastolic': {'value': 109},
                                'systolic': {'value': 181}},
                               {'date': '2021-09-13',
                                "receiptDate": "",
                                'dateFormatted': '9/13/2021',
                                'diastolic': {'value': 101},
                                'systolic': {'value': 160}},
                               {'date': '2021-09-13',
                                "receiptDate": "",
                                'dateFormatted': '9/13/2021',
                                'diastolic': {'value': 104},
                                'systolic': {'value': 120}},
                               {'date': '2021-05-13',
                                "receiptDate": "",
                                'dateFormatted': '5/13/2021',
                                'diastolic': {'value': 113},
                                'systolic': {'value': 131}}],
                 'oneYearBpReadings': 6,
                 'recentElevatedBpReadings': 3,
                 'totalBpReadings': 7,
                 'twoYearsBp': [{'date': '2021-10-14',
                                 "receiptDate": "",
                                 'dateFormatted': '10/14/2021',
                                 'diastolic': {'value': 111},
                                 'systolic': {'value': 155}},
                                {'date': '2021-10-13',
                                 "receiptDate": "",
                                 'dateFormatted': '10/13/2021',
                                 'diastolic': {'value': 116},
                                 'systolic': {'value': 180}},
                                {'date': '2021-10-10',
                                 "receiptDate": "",
                                 'dateFormatted': '10/10/2021',
                                 'diastolic': {'value': 109},
                                 'systolic': {'value': 181}},
                                {'date': '2021-09-13',
                                 "receiptDate": "",
                                 'dateFormatted': '9/13/2021',
                                 'diastolic': {'value': 101},
                                 'systolic': {'value': 160}},
                                {'date': '2021-09-13',
                                 "receiptDate": "",
                                 'dateFormatted': '9/13/2021',
                                 'diastolic': {'value': 104},
                                 'systolic': {'value': 120}},
                                {'date': '2021-05-13',
                                 "receiptDate": "",
                                 'dateFormatted': '5/13/2021',
                                 'diastolic': {'value': 113},
                                 'systolic': {'value': 131}},
                                {'date': '2020-11-08',
                                 "receiptDate": "",
                                 'dateFormatted': '11/8/2020',
                                 'diastolic': {'value': 105},
                                 'systolic': {'value': 154}}],
                 'twoYearsBpReadings': 7}
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
                                    "value": 90
                                },
                                "systolic": {
                                    "code": "8480-6",
                                    "display": "Systolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 180
                                },
                                "date": "2021-10-01",
                                "receiptDate": "",
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
                                "receiptDate": "",
                                "practitioner": "DR. JANE460 DOE922 MD",
                                "organization": "LYONS VA MEDICAL CENTER"
                            }
                        ]
                    }
                    ,
                    "claimSubmissionDateTime": "2021-11-09T17:45:59Z",
                },
                {'allBp': [{'date': '2021-10-01',
                            "receiptDate": "",
                            'dateFormatted': '10/1/2021',
                            'diastolic': {'code': '8462-4',
                                          'display': 'Diastolic blood pressure',
                                          'unit': 'mm[Hg]',
                                          'value': 90},
                            'organization': 'LYONS VA MEDICAL CENTER',
                            'practitioner': 'DR. JANE460 DOE922 MD',
                            'systolic': {'code': '8480-6',
                                         'display': 'Systolic blood pressure',
                                         'unit': 'mm[Hg]',
                                         'value': 180}},
                           {'date': '2021-09-01',
                            "receiptDate": "",
                            'dateFormatted': '9/1/2021',
                            'diastolic': {'code': '8462-4',
                                          'display': 'Diastolic blood pressure',
                                          'unit': 'mm[Hg]',
                                          'value': 110},
                            'organization': 'LYONS VA MEDICAL CENTER',
                            'practitioner': 'DR. JANE460 DOE922 MD',
                            'systolic': {'code': '8480-6',
                                         'display': 'Systolic blood pressure',
                                         'unit': 'mm[Hg]',
                                         'value': 200}}],
                 'oneYearBp': [{'date': '2021-10-01',
                                "receiptDate": "",
                                'dateFormatted': '10/1/2021',
                                'diastolic': {'code': '8462-4',
                                              'display': 'Diastolic blood pressure',
                                              'unit': 'mm[Hg]',
                                              'value': 90},
                                'organization': 'LYONS VA MEDICAL CENTER',
                                'practitioner': 'DR. JANE460 DOE922 MD',
                                'systolic': {'code': '8480-6',
                                             'display': 'Systolic blood pressure',
                                             'unit': 'mm[Hg]',
                                             'value': 180}},
                               {'date': '2021-09-01',
                                "receiptDate": "",
                                'dateFormatted': '9/1/2021',
                                'diastolic': {'code': '8462-4',
                                              'display': 'Diastolic blood pressure',
                                              'unit': 'mm[Hg]',
                                              'value': 110},
                                'organization': 'LYONS VA MEDICAL CENTER',
                                'practitioner': 'DR. JANE460 DOE922 MD',
                                'systolic': {'code': '8480-6',
                                             'display': 'Systolic blood pressure',
                                             'unit': 'mm[Hg]',
                                             'value': 200}}],
                 'oneYearBpReadings': 2,
                 'recentElevatedBpReadings': 1,
                 'totalBpReadings': 2,
                 'twoYearsBp': [{'date': '2021-10-01',
                                 'dateFormatted': '10/1/2021',
                                 "receiptDate": "",
                                 'diastolic': {'code': '8462-4',
                                               'display': 'Diastolic blood pressure',
                                               'unit': 'mm[Hg]',
                                               'value': 90},
                                 'organization': 'LYONS VA MEDICAL CENTER',
                                 'practitioner': 'DR. JANE460 DOE922 MD',
                                 'systolic': {'code': '8480-6',
                                              'display': 'Systolic blood pressure',
                                              'unit': 'mm[Hg]',
                                              'value': 180}},
                                {'date': '2021-09-01',
                                 "receiptDate": "",
                                 'dateFormatted': '9/1/2021',
                                 'diastolic': {'code': '8462-4',
                                               'display': 'Diastolic blood pressure',
                                               'unit': 'mm[Hg]',
                                               'value': 110},
                                 'organization': 'LYONS VA MEDICAL CENTER',
                                 'practitioner': 'DR. JANE460 DOE922 MD',
                                 'systolic': {'code': '8480-6',
                                              'display': 'Systolic blood pressure',
                                              'unit': 'mm[Hg]',
                                              'value': 200}}],
                 'twoYearsBpReadings': 2}
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
                                    "value": 115
                                },
                                "systolic": {
                                    "code": "8480-6",
                                    "display": "Systolic blood pressure",
                                    "unit": "mm[Hg]",
                                    "value": 180
                                },
                                "date": "2021-04-01",
                                "receiptDate": "",
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
                                "date": "2021-10-10",
                                "receiptDate": "",
                                "practitioner": "DR. JANE460 DOE922 MD",
                                "organization": "LYONS VA MEDICAL CENTER"
                            }
                        ]
                    }
                    ,
                    "claimSubmissionDateTime": "2021-11-09T17:45:59Z",
                },
                {'allBp': [{'date': '2021-10-10',
                            'dateFormatted': '10/10/2021',
                            "receiptDate": "",
                            'diastolic': {'code': '8462-4',
                                          'display': 'Diastolic blood pressure',
                                          'unit': 'mm[Hg]',
                                          'value': 110},
                            'organization': 'LYONS VA MEDICAL CENTER',
                            'practitioner': 'DR. JANE460 DOE922 MD',
                            'systolic': {'code': '8480-6',
                                         'display': 'Systolic blood pressure',
                                         'unit': 'mm[Hg]',
                                         'value': 200}},
                           {'date': '2021-04-01',
                            "receiptDate": "",
                            'dateFormatted': '4/1/2021',
                            'diastolic': {'code': '8462-4',
                                          'display': 'Diastolic blood pressure',
                                          'unit': 'mm[Hg]',
                                          'value': 115},
                            'organization': 'LYONS VA MEDICAL CENTER',
                            'practitioner': 'DR. JANE460 DOE922 MD',
                            'systolic': {'code': '8480-6',
                                         'display': 'Systolic blood pressure',
                                         'unit': 'mm[Hg]',
                                         'value': 180}}],
                 'oneYearBp': [{'date': '2021-10-10',
                                "receiptDate": "",
                                'dateFormatted': '10/10/2021',
                                'diastolic': {'code': '8462-4',
                                              'display': 'Diastolic blood pressure',
                                              'unit': 'mm[Hg]',
                                              'value': 110},
                                'organization': 'LYONS VA MEDICAL CENTER',
                                'practitioner': 'DR. JANE460 DOE922 MD',
                                'systolic': {'code': '8480-6',
                                             'display': 'Systolic blood pressure',
                                             'unit': 'mm[Hg]',
                                             'value': 200}},
                               {'date': '2021-04-01',
                                "receiptDate": "",
                                'dateFormatted': '4/1/2021',
                                'diastolic': {'code': '8462-4',
                                              'display': 'Diastolic blood pressure',
                                              'unit': 'mm[Hg]',
                                              'value': 115},
                                'organization': 'LYONS VA MEDICAL CENTER',
                                'practitioner': 'DR. JANE460 DOE922 MD',
                                'systolic': {'code': '8480-6',
                                             'display': 'Systolic blood pressure',
                                             'unit': 'mm[Hg]',
                                             'value': 180}}],
                 'oneYearBpReadings': 2,
                 'recentElevatedBpReadings': 2,
                 'totalBpReadings': 2,
                 'twoYearsBp': [{'date': '2021-10-10',
                                 "receiptDate": "",
                                 'dateFormatted': '10/10/2021',
                                 'diastolic': {'code': '8462-4',
                                               'display': 'Diastolic blood pressure',
                                               'unit': 'mm[Hg]',
                                               'value': 110},
                                 'organization': 'LYONS VA MEDICAL CENTER',
                                 'practitioner': 'DR. JANE460 DOE922 MD',
                                 'systolic': {'code': '8480-6',
                                              'display': 'Systolic blood pressure',
                                              'unit': 'mm[Hg]',
                                              'value': 200}},
                                {'date': '2021-04-01',
                                 "receiptDate": "",
                                 'dateFormatted': '4/1/2021',
                                 'diastolic': {'code': '8462-4',
                                               'display': 'Diastolic blood pressure',
                                               'unit': 'mm[Hg]',
                                               'value': 115},
                                 'organization': 'LYONS VA MEDICAL CENTER',
                                 'practitioner': 'DR. JANE460 DOE922 MD',
                                 'systolic': {'code': '8480-6',
                                              'display': 'Systolic blood pressure',
                                              'unit': 'mm[Hg]',
                                              'value': 180}}],
                 'twoYearsBpReadings': 2}
        ),
        # 1 reading
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
                                "receiptDate": "",
                                "date": "2021-11-01",
                                "practitioner": "DR. JANE460 DOE922 MD",
                                "organization": "LYONS VA MEDICAL CENTER"
                            }
                        ]
                    }
                    ,
                    "claimSubmissionDateTime": "2021-11-09T17:45:59Z",
                },
                {'allBp': [{'date': '2021-11-01',
                            'dateFormatted': '11/1/2021',
                            "receiptDate": "",
                            'diastolic': {'code': '8462-4',
                                          'display': 'Diastolic blood pressure',
                                          'unit': 'mm[Hg]',
                                          'value': 115},
                            'organization': 'LYONS VA MEDICAL CENTER',
                            'practitioner': 'DR. JANE460 DOE922 MD',
                            'systolic': {'code': '8480-6',
                                         'display': 'Systolic blood pressure',
                                         'unit': 'mm[Hg]',
                                         'value': 180}}],
                 'oneYearBp': [{'date': '2021-11-01',
                                "receiptDate": "",
                                'dateFormatted': '11/1/2021',
                                'diastolic': {'code': '8462-4',
                                              'display': 'Diastolic blood pressure',
                                              'unit': 'mm[Hg]',
                                              'value': 115},
                                'organization': 'LYONS VA MEDICAL CENTER',
                                'practitioner': 'DR. JANE460 DOE922 MD',
                                'systolic': {'code': '8480-6',
                                             'display': 'Systolic blood pressure',
                                             'unit': 'mm[Hg]',
                                             'value': 180}}],
                 'oneYearBpReadings': 1,
                 'recentElevatedBpReadings': 1,
                 'totalBpReadings': 1,
                 'twoYearsBp': [{'date': '2021-11-01',
                                 'dateFormatted': '11/1/2021',
                                 "receiptDate": "",
                                 'diastolic': {'code': '8462-4',
                                               'display': 'Diastolic blood pressure',
                                               'unit': 'mm[Hg]',
                                               'value': 115},
                                 'organization': 'LYONS VA MEDICAL CENTER',
                                 'practitioner': 'DR. JANE460 DOE922 MD',
                                 'systolic': {'code': '8480-6',
                                              'display': 'Systolic blood pressure',
                                              'unit': 'mm[Hg]',
                                              'value': 180}}],
                 'twoYearsBpReadings': 1}
        ),
        # 0 readings
        (
                {
                    "evidence": {
                        "bp_readings": []
                    },
                    "claimSubmissionDateTime": "2021-11-09T17:45:59Z",
                },
                {'allBp': [],
                 'oneYearBp': [],
                 'oneYearBpReadings': 0,
                 'recentElevatedBpReadings': 0,
                 'totalBpReadings': 0,
                 'twoYearsBp': [],
                 'twoYearsBpReadings': 0}
        )
    ],
)
def test_bp_reader(request_body, bp_calculator_result):
    """
    Test the history of blood pressure sufficiency algorithm

    :param request_body: request body with blood pressure readings and other data
    :type request_body: dict
    :param predominance_calculation: correct return value from algorithm
    :type predominance_calculation: dict
    """
    assert bp_calculator.bp_reader(request_body) == bp_calculator_result
