import pytest
from src.lib import continuous_medication


@pytest.mark.parametrize(
    "request_body, continuous_medication_required_calculation",
    [
        # Service connected and medication used to treat hypertension
        (
            {
                "observation": {"bp_readings": []},
                "medication": [{"text": "Benazepril"}],
                'date_of_claim': '2021-11-09',
            },
            {
                "relevant_medications": [{"text": "Benazepril"}]
            },
        ),
        # Not service connected but uses medication used to treat hypertension
        (
            {
                "observation": {"bp_readings": []},
                "medication": [{"text": "Benazepril"}],
                'date_of_claim': '2021-11-09',
            },
            {
                "relevant_medications": [{"text": "Benazepril"}]
            },
        ),
        # Service connected but doesn't use medication used to treat hypertension
        (
            {
                "observation": {"bp_readings": []},
                "medication": [{"text": "Advil"}],
                'date_of_claim': '2021-11-09',
            },
            {
                "relevant_medications": []
            },
        ),
        # Service connected, multiple medications, some to treat and others not to treat hypertension
        (
            {
                "observation": {"bp_readings": []},
                "medication": [{"text": "Benazepril"}, {"text": "Advil"}],
                'date_of_claim': '2021-11-09',
            },
            {
                "relevant_medications": [{"text": "Benazepril"}]

            },
        ),
        # Service connected but no medication
        (
            {
                "observation": {"bp_readings": []},
                "medication": [],
                'date_of_claim': '2021-11-09',
            },
            {
                "relevant_medications": []
            },
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