import pytest

from assessclaimdc7101.src.lib import bp_filter


@pytest.mark.parametrize(
    "request_body, recent_bp_readings",
    [
        (
            {
                "evidence": {
                    "bp_readings": [
                        {
                            "diastolic": {"value": 100},
                            "systolic": {"value": 180},
                            "date": "2021-11-01",
                        },
                        {
                            "diastolic": {"value": 100},
                            "systolic": {"value": 180},
                            "date": "2020-11-01",
                        },
                    ]
                },
                "dateOfClaim": "2021-11-02",
            },
            {
                "bpReadings": [
                    {
                        "date": "2021-11-01",
                        "diastolic": {"value": 100},
                        "systolic": {"value": 180},
                    }
                ],
                "recentBpReadings": 1,
                "totalBpReadings": 2,
            },
        ),
        (
            {
                "evidence": {
                    "bp_readings": [
                        {
                            "diastolic": {"value": 100},
                            "systolic": {"value": 180},
                            "date": "2021-11-01",
                        },
                        {
                            "diastolic": {"value": 100},
                            "systolic": {"value": 180},
                            "date": "2020-11-01",
                        },
                    ]
                },
                "dateOfClaim": "2022-11-02",
            },
            {"bpReadings": [], "recentBpReadings": 0, "totalBpReadings": 2},
        ),
    ],
)
def test_bp_filter(request_body, recent_bp_readings):

    assert bp_filter.bp_recency(request_body) == recent_bp_readings
