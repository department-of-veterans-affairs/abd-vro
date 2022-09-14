import pytest
from assessclaimdc6602.src.lib import condition


@pytest.mark.parametrize(
    "request_body, conditions_calc",
    [
        # Service connected and medication used to treat hypertension
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "conditions": [],
                    "medications": [{"description": "Albuterol"}],
                },
                "date_of_claim": "2021-11-09",
            },
            {
                "conditions": [],
                "persistent_calculation": {
                    "success": True,
                    "mild-persistent-asthma-or-greater": False,
                },
            },
        ),
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "conditions": [{"text": "Asthma", "code": "195967001"}],
                    "medications": [{"description": "Hydrochlorothiazide 25 MG"}],
                },
                "date_of_claim": "2021-11-09",
            },
            {
                "conditions": [{"text": "Asthma", "code": "195967001"}],
                "persistent_calculation": {
                    "success": True,
                    "mild-persistent-asthma-or-greater": False,
                },
            },
        ),
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "conditions": [{"text": "Eosinophilic asthma", "code": "J82.83"}],
                    "medications": [{"description": "Hydrochlorothiazide 25 MG"}],
                },
                "date_of_claim": "2021-11-09",
            },
            {
                "conditions": [{"text": "Eosinophilic asthma", "code": "J82.83"}],
                "persistent_calculation": {
                    "success": True,
                    "mild-persistent-asthma-or-greater": True,
                },
            },
        ),
    ],
)
def test_condtions_calculation(request_body, conditions_calc):
    """

    :param request_body:
    :param conditions_calc:
    :return:
    """
    active_conditons = condition.conditions_calculation(request_body)
    assert active_conditons == conditions_calc
