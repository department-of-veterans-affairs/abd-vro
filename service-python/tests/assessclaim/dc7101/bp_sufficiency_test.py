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
                            "receiptDate": "2022-11-01",
                            "date": "2021-11-01",
                            "dataSource": "MAS"
                        },
                        {
                            "diastolic": {
                                "value": 110
                            },
                            "systolic": {
                                "value": 200
                            },
                            "receiptDate": "2022-11-01",
                            "date": "2021-09-01",
                            "dataSource": "LH"
                        }
                    ]
                }
                    ,
                    "claimSubmissionDateTime": "2021-11-09T17:45:59Z",
                },
                {'allBp': [{'dataSource': 'MAS',
                            'date': '2021-11-01',
                            'dateFormatted': '11/1/2021',
                            'diastolic': {'value': 115},
                            'receiptDate': '11/1/2022',
                            'systolic': {'value': 180}},
                           {'dataSource': 'LH',
                            'date': '2021-09-01',
                            'dateFormatted': '9/1/2021',
                            'diastolic': {'value': 110},
                            'receiptDate': '11/1/2022',
                            'systolic': {'value': 200}}],
                 'oneYearBp': [{'dataSource': 'MAS',
                                'date': '2021-11-01',
                                'dateFormatted': '11/1/2021',
                                'diastolic': {'value': 115},
                                'receiptDate': '11/1/2022',
                                'systolic': {'value': 180}},
                               {'dataSource': 'LH',
                                'date': '2021-09-01',
                                'dateFormatted': '9/1/2021',
                                'diastolic': {'value': 110},
                                'receiptDate': '11/1/2022',
                                'systolic': {'value': 200}}],
                 'oneYearBpCount': 2,
                 'twoYearsElevatedBpCount': 2,
                 'totalBpCount': 2,
                 'twoYearsBp': [{'dataSource': 'MAS',
                                 'date': '2021-11-01',
                                 'dateFormatted': '11/1/2021',
                                 'diastolic': {'value': 115},
                                 'receiptDate': '11/1/2022',
                                 'systolic': {'value': 180}},
                                {'dataSource': 'LH',
                                 'date': '2021-09-01',
                                 'dateFormatted': '9/1/2021',
                                 'diastolic': {'value': 110},
                                 'receiptDate': '11/1/2022',
                                 'systolic': {'value': 200}}],
                 'twoYearsBpCount': 2}
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
                                "value": 0
                            },
                            "systolic": {
                                "value": 0
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
                            'dateFormatted': '9/1/2021',
                            'diastolic': {'value': 0},
                            'receiptDate': '',
                            'systolic': {'value': 0}},
                           {'date': '2020-11-09',
                            'dateFormatted': '11/9/2020',
                            'diastolic': {'value': 120},
                            'receiptDate': '',
                            'systolic': {'value': 210}},
                           {'date': '2019-11-01',
                            'dateFormatted': '11/1/2019',
                            'diastolic': {'value': 115},
                            'receiptDate': '',
                            'systolic': {'value': 180}}],
                 'oneYearBp': [{'date': '2020-11-09',
                                'dateFormatted': '11/9/2020',
                                'diastolic': {'value': 120},
                                'receiptDate': '',
                                'systolic': {'value': 210}}],
                 'oneYearBpCount': 1,
                 'totalBpCount': 3,
                 'twoYearsBp': [{'date': '2021-09-01',
                                 'dateFormatted': '9/1/2021',
                                 'diastolic': {'value': 0},
                                 'receiptDate': '',
                                 'systolic': {'value': 0}},
                                {'date': '2020-11-09',
                                 'dateFormatted': '11/9/2020',
                                 'diastolic': {'value': 120},
                                 'receiptDate': '',
                                 'systolic': {'value': 210}}],
                 'twoYearsBpCount': 1,
                 'twoYearsElevatedBpCount': 1}
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
                 'oneYearBpCount': 7,
                 'twoYearsElevatedBpCount': 4,
                 'totalBpCount': 7,
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
                 'twoYearsBpCount': 7}
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
                                "receiptDate": "2021-09-13",
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
                            "receiptDate": "9/13/2021",
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
                 'oneYearBpCount': 5,
                 'twoYearsElevatedBpCount': 3,
                 'totalBpCount': 6,
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
                 'twoYearsBpCount': 5}
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
                 'oneYearBpCount': 6,
                 'twoYearsElevatedBpCount': 3,
                 'totalBpCount': 7,
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
                 'twoYearsBpCount': 7}
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
                 'oneYearBpCount': 2,
                 'twoYearsElevatedBpCount': 1,
                 'totalBpCount': 2,
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
                 'twoYearsBpCount': 2}
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
                 'oneYearBpCount': 2,
                 'twoYearsElevatedBpCount': 2,
                 'totalBpCount': 2,
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
                 'twoYearsBpCount': 2}
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
                 'oneYearBpCount': 1,
                 'twoYearsElevatedBpCount': 1,
                 'totalBpCount': 1,
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
                 'twoYearsBpCount': 1}
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
                 'oneYearBpCount': 0,
                 'twoYearsElevatedBpCount': 0,
                 'totalBpCount': 0,
                 'twoYearsBp': [],
                 'twoYearsBpCount': 0}
        )
    ],
)
def test_bp_reader(request_body, bp_calculator_result):
    """
    Test the history of blood pressure sufficiency algorithm

    :param request_body: request body with blood pressure readings and other data
    :type request_body: dict
    :param bp_calculator_result: correct return value from algorithm
    :type bp_calculator_result: dict
    """
    assert bp_calculator.bp_reader(request_body) == bp_calculator_result
