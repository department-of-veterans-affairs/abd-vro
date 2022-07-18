import pytest
from src.lib import medication


@pytest.mark.parametrize(
    "request_body, continuous_medication_required_calculation",
    [
        # Service connected and medication used to treat hypertension
        (
            {
                "bp": [],
                "medication": [{"text": "Albuterol"}],
                'date_of_claim': '2021-11-09',
            },
            [{"text": "Albuterol"}],
        ),
        # Not service connected but uses medication used to treat hypertension
        (
            {
                "bp": [],
                "medication": [{"text": "Albuterol"}],
                'date_of_claim': '2021-11-09',
            },
            [{"text": "Albuterol"}],
        ),
        # Service connected but doesn't use medication used to treat hypertension
        (
            {
                "bp": [],
                "medication": [{"text": "Advil"}],
                'date_of_claim': '2021-11-09',
            },
            [],
        ),
        # multiple medications, some to treat and others not to treat asthma
        (
            {
                "bp": [],
                "medication": [{"text": "Albuterol"}, {"text": "Advil"}],
                'date_of_claim': '2021-11-09',
            },
            [{"text": "Albuterol"}],
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
    assert medication.medication_required(request_body) == continuous_medication_required_calculation
