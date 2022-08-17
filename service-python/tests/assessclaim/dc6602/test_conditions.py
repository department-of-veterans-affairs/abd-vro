import pytest
from assessclaimdc6602.src.lib import condition



@pytest.mark.parametrize(
     "request_body, continuous_medication_required_calculation",
    [
        # Service connected and medication used to treat hypertension
        (
            {"evidence":
                {
                    "bp_readings": [],
                    "medications": [{"description": "Albuterol"}],
                    'date_of_claim': '2021-11-09',
                }
            },
            [{"description": "Albuterol"}],
        ),
    ]
)
def test_condtions_calculation():
    """
    
    """