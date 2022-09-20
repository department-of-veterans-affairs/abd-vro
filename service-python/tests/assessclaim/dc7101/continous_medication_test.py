import pytest
from assessclaimdc7101.src.lib import continuous_medication


@pytest.mark.parametrize(
    "request_body, continuous_medication_required_calculation",
    [
        # Service connected and medication used to treat hypertension
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "medications": [
                        {
                            "description": "Benazepril",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        }
                    ],
                    "date_of_claim": "2021-11-09",
                }
            },
            {
                "medications": [
                    {
                        "authoredOn": "1950-04-06T04:00:00Z",
                        "description": "Benazepril",
                        "status": "active",
                    }
                ],
                "medicationsCount": 1,
            },
        ),
        # Not service connected but uses medication used to treat hypertension
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "medications": [
                        {
                            "description": "Benazepril",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        }
                    ],
                    "date_of_claim": "2021-11-09",
                }
            },
            {
                "medications": [
                    {
                        "authoredOn": "1950-04-06T04:00:00Z",
                        "description": "Benazepril",
                        "status": "active",
                    }
                ],
                "medicationsCount": 1,
            },
        ),
        # Service connected but doesn't use medication used to treat hypertension
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "medications": [
                        {
                            "description": "Advil",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        }
                    ],
                    "date_of_claim": "2021-11-09",
                }
            },
            {
                "medications": [
                    {
                        "authoredOn": "1950-04-06T04:00:00Z",
                        "description": "Advil",
                        "status": "active",
                    }
                ],
                "medicationsCount": 1,
            },
        ),
        # Service connected, multiple medications, some to treat and others not to treat hypertension
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "medications": [
                        {
                            "description": "Benazepril",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        },
                        {
                            "description": "Advil",
                            "status": "active",
                            "authoredOn": "1952-04-06T04:00:00Z",
                        },
                    ],
                    "date_of_claim": "2021-11-09",
                }
            },
            {
                "medications": [
                    {
                        "authoredOn": "1952-04-06T04:00:00Z",
                        "description": "Advil",
                        "status": "active",
                    },
                    {
                        "authoredOn": "1950-04-06T04:00:00Z",
                        "description": "Benazepril",
                        "status": "active",
                    },
                ],
                "medicationsCount": 2,
            },
        ),
        # Service connected but no medication
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "medications": [],
                }
            },
            {"medications": [], "medicationsCount": 0},
        ),
    ],
)
def test_continuous_medication_required(
    request_body, continuous_medication_required_calculation
):
    """
    Test the history of continuous medication required algorithm

    :param request_body: request body with blood pressure readings and other data
    :type request_body: dict
    :param continuous_medication_required_calculation: correct return value from algorithm
    :type continuous_medication_required_calculation: dict
    """
    assert (
        continuous_medication.continuous_medication_required(request_body)
        == continuous_medication_required_calculation
    )
