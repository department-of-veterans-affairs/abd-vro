import pytest
from assessclaimdc6522.src.lib import conditions


@pytest.mark.parametrize(
    "request_body, conditions_calc",
    [
        # Service connected and medication used to treat hypertension
        (
                {
                    "evidence": {
                        "conditions": [
                            {"text": "Granulomatous rhinitis (chronic)",
                             "code": "J31.0",
                             "status": "Active"
                             },
                            {
                                "text": "Nasal polyps",
                                "code": "J33.9",
                                "status": "Active"
                            }

                        ],
                    },
                    "date_of_claim": "2021-11-09",
                },
                {"conditions": [{
                                    "text": "Granulomatous rhinitis (chronic)",
                                    "code": "J31.0",
                                    "status": "Active"
                                 },
                                {
                                    "text": "Nasal polyps",
                                    "code": "J33.9",
                                    "status": "Active"}],
                 "relevantConditionsCount": 2,
                 "totalConditionsCount": 2,
                 "diagnosticCodes": ["6524"],
                 "nasalPolyps": True
                 }
        ),
        (
                {
                    "evidence": {
                        "conditions": [
                            {"text": "Allergic rhinitis due to food", "code": "J30.5", "status": "Active"},
                            {"code": "72409005", "text": "Rhinoscleroma (disorder)", "status": "recurrence"}
                        ],
                    },
                    "date_of_claim": "2021-11-09",
                },
                {"conditions": [{"text": "Allergic rhinitis due to food", "code": "J30.5", "status": "Active"
                                 }],
                 "relevantConditionsCount": 1,
                 "totalConditionsCount": 2,
                 "diagnosticCodes": ["6522"],
                 "nasalPolyps": False
                 }
        ),
        (
                {
                    "evidence": {
                        "conditions": [
                            {
                                "text": "Eosinophilic asthma",
                                "code": "J82.83",
                                "status": "Active",
                            }
                        ],
                        "date_of_claim": "2021-11-09",
                    }
                },
                {"conditions": [],
                 "relevantConditionsCount": 0,
                 "totalConditionsCount": 1,
                 "diagnosticCodes": [],
                 "nasalPolyps": False
                 }
        ),
    ],
)
def test_conditions_calculation(request_body, conditions_calc):
    """
    Test the filtering of conditions for Asthma
    :param request_body: sample data for a claim reqeust
    :param conditions_calc:e expected output from the condition algorithm
    """
    active_conditions = conditions.conditions_calculation(request_body)
    assert active_conditions == conditions_calc
