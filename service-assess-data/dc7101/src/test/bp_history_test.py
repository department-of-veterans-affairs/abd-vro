import pytest
from dc7101.lib import bp_history


@pytest.mark.parametrize(
    "request_body, diastolic_bp_predominantly_100_or_more",
    [
        # 0 readings
        (
            {
                "observation": {"bp": []}
            },
            {
                "success": False
            }
        ),
        # 1 reading test case that passes
        (
            {
                "observation": {
                        "bp": [
                        {
                            "diastolic": 100,
                            "systolic": 180,
                            "date": "2021-11-01"
                        },
                    ]
                }
            },
            {
                "diastolic_bp_predominantly_100_or_more": True,
                "success": True 
            }
        ),
        # 1 reading test case that fails
        (
            {
                "observation": {
                    "bp": [
                        {
                            "diastolic": 90,
                            "systolic": 180,
                            "date": "2021-11-01"
                        },
                    ]
                }
            },
            {
                "diastolic_bp_predominantly_100_or_more": False,
                "success": True 
            }
        ),
        # 2 reading test case that passes
        (
            {
                "observation": {
                    "bp": [
                        {
                            "diastolic": 100,
                            "systolic": 180,
                            "date": "2021-11-01"
                        },
                        {
                            "diastolic": 90,
                            "systolic": 200,
                            "date": "2021-09-01"
                        }
                    ]
                }
            },
            {
                "diastolic_bp_predominantly_100_or_more": True,
                "success": True 
            }
        ),
        # 2 reading test case that fails
        (
            {
                "observation": {
                    "bp": [
                        {
                            "diastolic": 90,
                            "systolic": 180,
                            "date": "2021-11-01"
                        },
                        {
                            "diastolic": 90,
                            "systolic": 200,
                            "date": "2021-09-01"
                        }
                    ]
                }
            },
            {
                "diastolic_bp_predominantly_100_or_more": False,
                "success": True 
            }
        ),
        # 3 reading test case that passes
        (
            {
                "observation": {
                    "bp": [
                        {
                            "diastolic": 101,
                            "systolic": 180,
                            "date": "2021-11-01"
                        },
                        {
                            "diastolic": 90,
                            "systolic": 200,
                            "date": "2021-09-01"
                        },
                        {
                            "diastolic": 115,
                            "systolic": 200,
                            "date": "2021-09-02"
                        }
                    ]
                }
            },
            {
                "diastolic_bp_predominantly_100_or_more": True,
                "success": True 
            }
        ),
        # 3 reading test case that fails
        (
            {
                "observation": {
                    "bp": [
                        {
                            "diastolic": 101,
                            "systolic": 180,
                            "date": "2021-11-01"
                        },
                        {
                            "diastolic": 90,
                            "systolic": 200,
                            "date": "2021-09-01"
                        },
                        {
                            "diastolic": 95,
                            "systolic": 200,
                            "date": "2021-09-02"
                        }
                    ]
                }
            },
            {
                "diastolic_bp_predominantly_100_or_more": False,
                "success": True 
            }
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
