import pytest
from assessclaimdc6510.src.lib import condition


@pytest.mark.parametrize(
    "request_body, filtered_condition",
    [
        (
                {
                    "evidence": {
                        "conditions": [
                            {"text": "Chronic maxillary sinusitis",
                             "code": "35923002",
                             "status": "Active"}
                        ],
                        "date_of_claim": "2021-11-09",
                    }
                },
                {
                    "conditions": [
                        {
                            "text": "Chronic maxillary sinusitis",
                            "code": "35923002",
                            "status": "Active"
                        }
                    ],
                    "relevantConditionsCount": 1,
                    "totalConditionsCount": 1,
                },
        ),
        (
                {
                    "evidence": {
                        "conditions": [
                            {"text": "Asthma",
                             "code": "J45",
                             "status": "Active"}
                        ],
                        "date_of_claim": "2021-11-09",
                    }
                },
                {
                    "conditions": [
                    ],
                    "relevantConditionsCount": 0,
                    "totalConditionsCount": 1,
                },
        ),
        (
                {
                    "evidence": {
                        "conditions": [
                            {"text": "Chronic maxillary sinusitis",
                             "code": "35923002",
                             "status": "Active"},
                            {"text": "Asthma",
                             "code": "J45",
                             "status": "Active"},
                        ],
                        "date_of_claim": "2021-11-09",
                    }
                },
                {
                    "conditions": [
                        {"text": "Chronic maxillary sinusitis",
                         "code": "35923002",
                         "status": "Active"}
                    ],
                    "relevantConditionsCount": 1,
                    "totalConditionsCount": 2,
                },
        ),
        (
                {
                    "evidence": {
                        "conditions": [
                            {"text": "Chronic maxillary sinusitis",
                             "code": "35923002",
                             "status": "Active"},
                            {"text": "Chronic maxillary sinusitis",
                             "code": "J32.0",
                             "status": "Active"},
                        ],
                        "date_of_claim": "2021-11-09",
                    }
                },
                {
                    "conditions": [
                        {"text": "Chronic maxillary sinusitis",
                         "code": "35923002",
                         "status": "Active"},
                        {"text": "Chronic maxillary sinusitis",
                         "code": "J32.0",
                         "status": "Active"},
                    ],
                    "relevantConditionsCount": 2,
                    "totalConditionsCount": 2,
                },
        ),
    ],
)
def test_condition(
        request_body, filtered_condition
):
    """
    Test the condition filtering algorithm

    :param request_body: request body with blood pressure readings and other data
    :type request_body: dict
    :param filtered_condition: correct return value from algorithm
    :type filtered_condition: dict
    """
    assert (
            condition.conditions_calculation(request_body)
            == filtered_condition
    )
