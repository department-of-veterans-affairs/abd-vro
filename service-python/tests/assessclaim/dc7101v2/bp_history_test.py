import pytest
from assessclaimdc7101v2.src.lib import bp_history


@pytest.mark.parametrize(
    "request_body, diastolic_bp_predominantly_100_or_more",
    [
        # 0 readings
        (
                {"evidence":
                    {
                        "bp_readings": []
                    }

                },
                {'calculated': {'success': False}, 'totalBpReadings': 0}
        ),
        # 1 reading test case that passes
        (
                {
                    "evidence": {
                        "bp_readings": [
                            {
                                "diastolic": {"value": 100},
                                "systolic": {"value": 180},
                                "date": "2021-11-01"
                            },
                        ]
                    }
                },
                {'calculated': {'diastolic_bp_predominantly_100_or_more': True,
                                'success': True},
                 'totalBpReadings': 1}
        ),
        # 1 reading test case that fails
        (
                {
                    "evidence": {

                        "bp_readings": [
                            {
                                "diastolic": {"value": 90},
                                "systolic": {"value": 180},
                                "date": "2021-11-01"
                            },
                        ]
                    }
                },
                {'calculated': {'diastolic_bp_predominantly_100_or_more': False,
                                'success': True},
                 'totalBpReadings': 1}
        ),
        # 2 reading test case that passes
        (
                {
                    "evidence": {

                        "bp_readings": [
                            {
                                "diastolic": {"value": 100},
                                "systolic": {"value": 180},
                                "date": "2021-11-01"
                            },
                            {
                                "diastolic": {"value": 90},
                                "systolic": {"value": 200},
                                "date": "2021-09-01"
                            }
                        ]
                    }
                },
                {'calculated': {'diastolic_bp_predominantly_100_or_more': True,
                                'success': True},
                 'totalBpReadings': 2}
        ),
        # 2 reading test case that fails
        (
                {
                    "evidence": {
                        "bp_readings": [
                            {
                                "diastolic": {"value": 90},
                                "systolic": {"value": 180},
                                "date": "2021-11-01"
                            },
                            {
                                "diastolic": {"value": 90},
                                "systolic": {"value": 200},
                                "date": "2021-09-01"
                            }
                        ]
                    }
                },
                {'calculated': {'diastolic_bp_predominantly_100_or_more': False,
                                'success': True},
                 'totalBpReadings': 2}
        ),
        # 3 reading test case that passes
        (
                {
                    "evidence":
                        {

                            "bp_readings": [
                                {
                                    "diastolic": {"value": 101},
                                    "systolic": {"value": 180},
                                    "date": "2021-11-01"
                                },
                                {
                                    "diastolic": {"value": 90},
                                    "systolic": {"value": 200},
                                    "date": "2021-09-01"
                                },
                                {
                                    "diastolic": {"value": 115},
                                    "systolic": {"value": 200},
                                    "date": "2021-09-02"
                                }
                            ]
                        }
                },
                {'calculated': {'diastolic_bp_predominantly_100_or_more': True,
                                'success': True},
                 'totalBpReadings': 3}
        ),
        # 3 reading test case that fails
        (
                {
                    "evidence": {
                        "bp_readings": [
                            {
                                "diastolic": {"value": 101},
                                "systolic": {"value": 180},
                                "date": "2021-11-01"
                            },
                            {
                                "diastolic": {"value": 90},
                                "systolic": {"value": 200},
                                "date": "2021-09-01"
                            },
                            {
                                "diastolic": {"value": 95},
                                "systolic": {"value": 200},
                                "date": "2021-09-02"
                            }
                        ]
                    }

                },
                {'calculated': {'diastolic_bp_predominantly_100_or_more': False,
                                'success': True},
                 'totalBpReadings': 3}
        ),
    ],
)
def test_history_of_diastolic_bp(request_body, diastolic_bp_predominantly_100_or_more):
    """
    Test the history of blood pressure algorithm

    :param request_body: request body with blood pressure readings and other data
    :type request_body: dict
    :param diastolic_bp_predominantly_100_or_more: correct return value from algorithm 
    :type diastolic_bp_predominantly_100_or_more: dict
    """
    assert bp_history.history_of_diastolic_bp(request_body) == diastolic_bp_predominantly_100_or_more
