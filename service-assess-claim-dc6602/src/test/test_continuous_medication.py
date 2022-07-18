import pytest
from dc6602.src.lib import medication


@pytest.mark.parametrize(
    "request_body, continuous_medication_required_calculation",
    [
        # Service connected and medication used to treat hypertension
        (
            {
                "bp": [],
                "medication": ["Benazepril"],
                'date_of_claim': '2021-11-09',
            },
            {
                "success": True,
                "continuous_medication_required": True
            },
        ),
        # Not service connected but uses medication used to treat hypertension
        (
            {
                "bp": [],
                "medication": ["Benazepril"],
                'date_of_claim': '2021-11-09',
            },
            {
                "success": True,
                "continuous_medication_required": True
            },
        ),
        # Service connected but doesn't use medication used to treat hypertension
        (
            {
                "bp": [],
                "medication": ["Advil"],
                'date_of_claim': '2021-11-09',
            },
            {
                "success": True,
                "continuous_medication_required": False
            },
        ),
        # Service connected, multiple medications, some to treat and others not to treat hypertension
        (
            {
                "bp": [],
                "medication": ["Benazepril", "Advil"],
                'date_of_claim': '2021-11-09',
            },
            {
                "success": True,
                "continuous_medication_required": True
            },
        ),
        # Service connected but no medication
        (
            {
                "bp": [],
                "medication": [],
                'date_of_claim': '2021-11-09',
            },
            {
                "success": True,
                "continuous_medication_required": False
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
    assert medication.medication_required(request_body) == continuous_medication_required_calculation
