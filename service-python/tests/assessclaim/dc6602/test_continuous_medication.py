import pytest
from assessclaimdc6602.src.lib import medication


@pytest.mark.parametrize(
    "request_body, continuous_medication_required_calculation",
    [
        # Service connected and medication used to treat hypertension
        (
            {"evidence":
                {
                    "medications": [{"description": "Albuterol"}],
                    'date_of_claim': '2021-11-09',
                }
            },
            [{"description": "Albuterol"}],
        ),
        # Not service connected but uses medication used to treat hypertension
        (
            {"evidence":
                {
                    "medications": [{"description": "Albuterol"}],
                    'date_of_claim': '2021-11-09',
                }
            },
            [{"description": "Albuterol"}],
        ),
        # Service connected but doesn't use medication used to treat hypertension
        (
            {"evidence":
                {
                    "medications": [{"description": "Advil"}],
                    'date_of_claim': '2021-11-09',
                }
            },
            [],
        ),
        # multiple medications, some to treat and others not to treat asthma
        (
            {"evidence":
                {
                    "medications": [{"description": "Albuterol"}, {"description": "Advil"}],
                    'date_of_claim': '2021-11-09',
                }
            },
            [{"description": "Albuterol"}],
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
