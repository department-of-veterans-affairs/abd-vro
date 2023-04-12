import pytest

from assessclaimdc6602v2.src.lib import condition


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
                "dateOfClaim": "2021-11-09",
            },
            {"conditions": [], "relevantConditionsCount": 0, "totalConditionsCount": 0},
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
                "dateOfClaim": "2021-11-09",
            },
            {"conditions": [{"code": "J82.83",
                             "status": "Active",
                             "text": "Eosinophilic asthma"}],
             "relevantConditionsCount": 1,
             "totalConditionsCount": 1}
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
