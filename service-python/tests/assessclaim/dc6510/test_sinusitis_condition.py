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
                             "status": "active",
                             "onsetDate": "2021-11-01"}
                        ],
                    },
                    "dateOfClaim": "2021-11-09",
                },
                {
                    "conditions": [],
                    "constantSinusitis": False,
                    "relevantConditionsCount": 0,
                    "totalConditionsCount": 1,
                    "osteomyelitis": False,
                },
        ),
        (
                {
                    "evidence": {
                        "conditions": [
                            {"text": "Asthma",
                             "code": "J45",
                             "status": "active",
                             "onsetDate": "2021-11-01"}
                        ],
                    },
                    "dateOfClaim": "2021-11-09",
                },
                {
                    "conditions": [
                    ],
                    "relevantConditionsCount": 0,
                    "totalConditionsCount": 1,
                    "constantSinusitis": False,
                    "osteomyelitis": False,
                },
        ),
        (
                {
                    "evidence": {
                        "conditions": [
                            {"text": "Chronic maxillary sinusitis",
                             "code": "35923002",
                             "status": "active",
                             "onsetDate": "2021-11-01"},
                            {"text": "Asthma",
                             "code": "J45",
                             "status": "active",
                             "onsetDate": "2021-11-01"},
                        ],
                    },
                    "dateOfClaim": "2022-06-09",
                },
                {
                    "conditions": [],
                    "relevantConditionsCount": 0,
                    "totalConditionsCount": 2,
                    "constantSinusitis": False,
                    "osteomyelitis": False,
                },
        ),
        (
                {
                    "evidence": {
                        "conditions": [
                            {"text": "Osteomyelitis",
                             "code": "H05.029",
                             "status": "active",
                             "onsetDate": "2021-11-01"},
                            {"text": "Chronic maxillary sinusitis",
                             "code": "J32.0",
                             "status": "active",
                             "onsetDate": "2021-11-01"},
                        ]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                {
                    "conditions": [
                        {"text": "Osteomyelitis",
                         "code": "H05.029",
                         "status": "active",
                         "onsetDate": "2021-11-01"},
                        {"text": "Chronic maxillary sinusitis",
                         "code": "J32.0",
                         "status": "active",
                         "onsetDate": "2021-11-01"},
                    ],
                    "relevantConditionsCount": 2,
                    "totalConditionsCount": 2,
                    "constantSinusitis": False,
                    "osteomyelitis": True,
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
