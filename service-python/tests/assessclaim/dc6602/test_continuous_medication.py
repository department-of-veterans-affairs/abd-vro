import pytest
from assessclaimdc6602.src.lib import medication


@pytest.mark.parametrize(
    "request_body, continuous_medication_required_calculation",
    [
        # Service connected and medication used to treat hypertension
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "Albuterol inhaler",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        }
                    ],
                    "date_of_claim": "2021-11-09",
                }
            },
            [
                {
                    "description": "Albuterol inhaler",
                    "status": "active",
                    "asthma_relevant": "true",
                    "authoredOn": "1950-04-06T04:00:00Z",
                }
            ],
        ),
        # Not service connected but uses medication used to treat hypertension
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "Albuterol",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        }
                    ],
                    "date_of_claim": "2021-11-09",
                }
            },
            [
                {
                    "description": "Albuterol",
                    "status": "active",
                    "asthma_relevant": "true",
                    "authoredOn": "1950-04-06T04:00:00Z",
                }
            ],
        ),
        # Service connected but doesn't use medication used to treat hypertension
        (
            {
                "evidence": {
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
            [
                {
                    "description": "Advil",
                    "status": "active",
                    "asthma_relevant": "false",
                    "authoredOn": "1950-04-06T04:00:00Z",
                }
            ],
        ),
        # multiple medications, some to treat and others not to treat asthma
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "Albuterol",
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
            [
                {
                    "description": "Albuterol",
                    "status": "active",
                    "asthma_relevant": "true",
                    "authoredOn": "1950-04-06T04:00:00Z",
                },
                {
                    "description": "Advil",
                    "status": "active",
                    "asthma_relevant": "false",
                    "authoredOn": "1952-04-06T04:00:00Z",
                },
            ],
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
        medication.medication_required(request_body)
        == continuous_medication_required_calculation
    )
