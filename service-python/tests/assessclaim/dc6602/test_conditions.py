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
            [],
        ),
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "conditions": [
                        {"text": "Asthma", "code": "195967001", "status": "Active"}
                    ],
                    "medications": [{"description": "Hydrochlorothiazide 25 MG"}],
                },
                "date_of_claim": "2021-11-09",
            },
            [{"text": "Asthma", "code": "195967001", "status": "Active"}],
        ),
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "conditions": [
                        {
                            "text": "Eosinophilic asthma",
                            "code": "J82.83",
                            "status": "Active",
                        }
                    ],
                    "medications": [{"description": "Hydrochlorothiazide 25 MG"}],
                },
                "date_of_claim": "2021-11-09",
            },
            [{"text": "Eosinophilic asthma", "code": "J82.83", "status": "Active"}],
        ),
    ],
)
def test_conditions_calculation(request_body, conditions_calc):
    """
    Test the filtering of conditions for Asthma

    :param request_body: sample data for a claim reqeust
    :param conditions_calc:e expected output from the condition algorithm
    """
    active_conditions = condition.conditions_calculation(request_body)
    assert active_conditions == conditions_calc
