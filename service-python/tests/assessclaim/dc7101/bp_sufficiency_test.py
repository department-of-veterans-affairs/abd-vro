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
                            "date": "2021-11-01"
                        },
                        {
                            "diastolic": {
                                "value": 110
                            },
                            "systolic": {
                                "value": 200
                            },
                            "date": "2021-09-01"
                        }
                    ]
                }
                    ,
                    "dateOfClaim": "2021-11-09",
                },
                {'bp_readings': [{'date': '2021-11-01',
                                  'diastolic': {'value': 115},
                                  'systolic': {'value': 180}},
                                 {'date': '2021-09-01',
                                  'diastolic': {'value': 110},
                                  'systolic': {'value': 200}}],
                 'oneYearBpReadings': 2,
                 'recentElevatedBpReadings': 2,
                 'totalBpReadings': 2,
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
                            "date": "2019-11-01"
                        },
                        {
                            "diastolic": {
                                "value": 110
                            },
                            "systolic": {
                                "value": 200
                            },
                            "date": "2021-09-01"
                        },
                        {
                            "diastolic": {
                                "value": 120
                            },
                            "systolic": {
                                "value": 210
                            },
                            "date": "2020-11-09"
                        }
                    ]
                }
                    ,
                    "dateOfClaim": "2021-11-09",
                },
                {'bp_readings': [{'date': '2021-09-01',
                                  'diastolic': {'value': 110},
                                  'systolic': {'value': 200}},
                                 {'date': '2020-11-09',
                                  'diastolic': {'value': 120},
                                  'systolic': {'value': 210}}],

                 'oneYearBpReadings': 2,
                 'recentElevatedBpReadings': 2,
                 'totalBpReadings': 3,
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
                                "date": "2021-10-09"
                            },
                            {
                                "diastolic": {
                                    "value": 109
                                },
                                "systolic": {
                                    "value": 181
                                },
                                "date": "2021-10-10"
                            },
                            {
                                "diastolic": {
                                    "value": 113
                                },
                                "systolic": {
                                    "value": 131
                                },
                                "date": "2021-05-13"
                            },
                            {
                                "diastolic": {
                                    "value": 101
                                },
                                "systolic": {
                                    "value": 160
                                },
                                "date": "2021-09-13"
                            },
                            {
                                "diastolic": {
                                    "value": 104
                                },
                                "systolic": {
                                    "value": 120
                                },
                                "date": "2021-09-13"
                            },
                            {
                                "diastolic": {
                                    "value": 116
                                },
                                "systolic": {
                                    "value": 180
                                },
                                "date": "2021-10-13"
                            },
                            {
                                "diastolic": {
                                    "value": 111
                                },
                                "systolic": {
                                    "value": 155
                                },
                                "date": "2021-10-14"
                            }
                        ]
                    }
                    ,
                    "dateOfClaim": "2021-11-09",
                },
                {'bp_readings': [{'date': '2021-10-09',
                                  'diastolic': {'value': 112},
                                  'systolic': {'value': 181}},
                                 {'date': '2021-10-10',
                                  'diastolic': {'value': 109},
                                  'systolic': {'value': 181}},
                                 {'date': '2021-05-13',
                                  'diastolic': {'value': 113},
                                  'systolic': {'value': 131}},
                                 {'date': '2021-09-13',
                                  'diastolic': {'value': 101},
                                  'systolic': {'value': 160}},
                                 {'date': '2021-09-13',
                                  'diastolic': {'value': 104},
                                  'systolic': {'value': 120}},
                                 {'date': '2021-10-13',
                                  'diastolic': {'value': 116},
                                  'systolic': {'value': 180}},
                                 {'date': '2021-10-14',
                                  'diastolic': {'value': 111},
                                  'systolic': {'value': 155}}],
                 'oneYearBpReadings': 7,
                 'recentElevatedBpReadings': 4,
                 'totalBpReadings': 7,
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
                                "date": "2021-10-10"
                            },
                            {
                                "diastolic": {
                                    "value": 113
                                },
                                "systolic": {
                                    "value": 131
                                },
                                "date": "2021-05-13"
                            },
                            {
                                "diastolic": {
                                    "value": 101
                                },
                                "systolic": {
                                    "value": 160
                                },
                                "date": "2021-09-13"
                            },
                            {
                                "diastolic": {
                                    "value": 104
                                },
                                "systolic": {
                                    "value": 120
                                },
                                "date": "2021-09-13"
                            },
                            {
                                "diastolic": {
                                    "value": 116
                                },
                                "systolic": {
                                    "value": 180
                                },
                                "date": "2021-10-13"
                            },
                            {
                                "diastolic": {
                                    "value": 111
                                },
                                "systolic": {
                                    "value": 155
                                },
                                "date": "2021-10-14"
                            }
                        ]
                    }
                    ,
                    "dateOfClaim": "2021-11-09",
                },
                {'bp_readings': [{'date': '2021-10-10',
                                  'diastolic': {'value': 109},
                                  'systolic': {'value': 181}},
                                 {'date': '2021-05-13',
                                  'diastolic': {'value': 113},
                                  'systolic': {'value': 131}},
                                 {'date': '2021-09-13',
                                  'diastolic': {'value': 101},
                                  'systolic': {'value': 160}},
                                 {'date': '2021-09-13',
                                  'diastolic': {'value': 104},
                                  'systolic': {'value': 120}},
                                 {'date': '2021-10-13',
                                  'diastolic': {'value': 116},
                                  'systolic': {'value': 180}},
                                 {'date': '2021-10-14',
                                  'diastolic': {'value': 111},
                                  'systolic': {'value': 155}}],
                 'oneYearBpReadings': 6,
                 'recentElevatedBpReadings': 3,
                 'totalBpReadings': 6,
                 'twoYearsBpReadings': 6}
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
                                "date": "2021-10-10"
                            },
                            {
                                "diastolic": {
                                    "value": 113
                                },
                                "systolic": {
                                    "value": 131
                                },
                                "date": "2021-05-13"
                            },
                            {
                                "diastolic": {
                                    "value": 101
                                },
                                "systolic": {
                                    "value": 160
                                },
                                "date": "2021-09-13"
                            },
                            {
                                "diastolic": {
                                    "value": 104
                                },
                                "systolic": {
                                    "value": 120
                                },
                                "date": "2021-09-13"
                            },
                            {
                                "diastolic": {
                                    "value": 116
                                },
                                "systolic": {
                                    "value": 180
                                },
                                "date": "2021-10-13"
                            },
                            {
                                "diastolic": {
                                    "value": 111
                                },
                                "systolic": {
                                    "value": 155
                                },
                                "date": "2021-10-14"
                            },
                            {
                                "diastolic": {
                                    "value": 105
                                },
                                "systolic": {
                                    "value": 154
                                },
                                "date": "2020-11-08"
                            }
                        ]
                    }
                    ,
                    "dateOfClaim": "2021-11-09",
                },
                {'bp_readings': [{'date': '2021-10-10',
                                  'diastolic': {'value': 109},
                                  'systolic': {'value': 181}},
                                 {'date': '2021-05-13',
                                  'diastolic': {'value': 113},
                                  'systolic': {'value': 131}},
                                 {'date': '2021-09-13',
                                  'diastolic': {'value': 101},
                                  'systolic': {'value': 160}},
                                 {'date': '2021-09-13',
                                  'diastolic': {'value': 104},
                                  'systolic': {'value': 120}},
                                 {'date': '2021-10-13',
                                  'diastolic': {'value': 116},
                                  'systolic': {'value': 180}},
                                 {'date': '2021-10-14',
                                  'diastolic': {'value': 111},
                                  'systolic': {'value': 155}}],
                 'oneYearBpReadings': 6,
                 'recentElevatedBpReadings': 3,
                 'totalBpReadings': 7,
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
                        ]
                    }
                    ,
                    "dateOfClaim": "2021-11-09",
                },
                {'bp_readings': [{'date': '2021-10-01',
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
                                "practitioner": "DR. JANE460 DOE922 MD",
                                "organization": "LYONS VA MEDICAL CENTER"
                            }
                        ]
                    }
                    ,
                    "dateOfClaim": "2021-11-09",
                },
                {'bp_readings': [{'date': '2021-04-01',
                                  'diastolic': {'code': '8462-4',
                                                'display': 'Diastolic blood pressure',
                                                'unit': 'mm[Hg]',
                                                'value': 115},
                                  'organization': 'LYONS VA MEDICAL CENTER',
                                  'practitioner': 'DR. JANE460 DOE922 MD',
                                  'systolic': {'code': '8480-6',
                                               'display': 'Systolic blood pressure',
                                               'unit': 'mm[Hg]',
                                               'value': 180}},
                                 {'date': '2021-10-10',
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
                 'recentElevatedBpReadings': 2,
                 'totalBpReadings': 2,
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
                                "date": "2021-11-01",
                                "practitioner": "DR. JANE460 DOE922 MD",
                                "organization": "LYONS VA MEDICAL CENTER"
                            }
                        ]
                    }
                    ,
                    "dateOfClaim": "2021-11-09",
                },
                {'bp_readings': [{'date': '2021-11-01',
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
                 'twoYearsBpReadings': 1}
        ),
        # 0 readings
        (
                {
                    "evidence": {
                        "bp_readings": []
                    },
                    "dateOfClaim": "2021-11-09",
                },
                {'bp_readings': [],
                 'oneYearBpReadings': 0,
                 'recentElevatedBpReadings': 0,
                 'totalBpReadings': 0,
                 'twoYearsBpReadings': 0}
        )
    ],
)
def test_sufficient_for_fast_track(request_body, bp_calculator_result):
    """
    Test the history of blood pressure sufficiency algorithm

    :param request_body: request body with blood pressure readings and other data
    :type request_body: dict
    :param predominance_calculation: correct return value from algorithm
    :type predominance_calculation: dict
    """
    assert bp_calculator.sufficient_for_fast_track(request_body) == bp_calculator_result
