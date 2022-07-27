import pytest
from src.lib import continuous_medication


@pytest.mark.parametrize(
    "request_body, continuous_medication_required_calculation",
    [
        # Service connected and medication used to treat hypertension
        (
            {
                "bp_readings": [],
                "medication": [{"description": "Benazepril"}],
                'date_of_claim': '2021-11-09',
            },
            [{"description": "Benazepril"}]
            ,
        ),
        # Not service connected but uses medication used to treat hypertension
        (
            {
                "bp_readings": [],
                "medication": [{"description": "Benazepril"}],
                'date_of_claim': '2021-11-09',
            },
            [{"description": "Benazepril"}]
            ,
        ),
        # Service connected but doesn't use medication used to treat hypertension
        (
            {
                "bp_readings": [],
                "medication": [{"description": "Advil"}],
                'date_of_claim': '2021-11-09',
            },
            [],
        ),
        # Service connected, multiple medications, some to treat and others not to treat hypertension
        (
            {
                "bp_readings": [],
                "medication": [{"description": "Benazepril"}, {"description": "Advil"}],
                'date_of_claim': '2021-11-09',
            },
            [{"description": "Benazepril"}],
        ),
        # Service connected but no medication
        (
            {
                "bp_readings": [],
                "medication": [],
                'date_of_claim': '2021-11-09',
            },
            [],
        ),
    ],
)
def test_continuous_medication_required(request_body, continuous_medication_required_calculation):
    """
    Test the history of continuous medication required algorithm

    :param request_body: request body with blood pressure readings and other data
    :type request_body: dict
    :param continuous_medication_required_calculation: correct return value from algorithm
    :type continuous_medication_required_calculation: dict
    """
    assert continuous_medication.continuous_medication_required(request_body) == continuous_medication_required_calculation