import pytest

from assessclaimdc7101.src.lib import continuous_medication


@pytest.mark.parametrize(
    "request_body, continuous_medication_required_calculation",
    [
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "medications": [
                        {
                            "description": "Benazepril",
                            "status": "active",
                            "authoredOn": "2020-04-06T04:00:00Z",
                        }
                    ],
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                }
            },
            {
                "medications": [
                    {
                        "authoredOn": "2020-04-06T04:00:00Z",
                        "description": "Benazepril",
                        "status": "active",
                    }
                ],
                "medicationsCount": 1,
            },
        ),
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "medications": [
                        {
                            "description": "Benazepril",
                            "status": "active",
                            "authoredOn": "2020-04-06T04:00:00Z",
                        }
                    ],
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                }
            },
            {
                "medications": [
                    {
                        "authoredOn": "2020-04-06T04:00:00Z",
                        "description": "Benazepril",
                        "status": "active",
                    }
                ],
                "medicationsCount": 1,
            },
        ),
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "medications": [
                        {
                            "description": "Advil",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        }
                    ],
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                }
            },
            {
                "medications": [
                    {
                        "authoredOn": "1950-04-06T04:00:00Z",
                        "description": "Advil",
                        "status": "active",
                    }
                ],
                "medicationsCount": 1,
            },
        ),
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "medications": [
                        {
                            "description": "Benazepril",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        },
                        {
                            "description": "Advil",
                            "status": "active",
                            "authoredOn": "1952-04-06T04:00:00Z",
                        },
                    ],
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                }
            },
            {
                "medications": [
                    {
                        "authoredOn": "1952-04-06T04:00:00Z",
                        "description": "Advil",
                        "status": "active",
                    },
                    {
                        "authoredOn": "1950-04-06T04:00:00Z",
                        "description": "Benazepril",
                        "status": "active",
                    },
                ],
                "medicationsCount": 2,
            },
        ),
        # Service connected but no medication
        (
            {
                "evidence": {
                    "bp_readings": [],
                    "medications": [],
                }
            },
            {"medications": [], "medicationsCount": 0},
        ),
    ],
)
def test_continuous_medication_required(
    request_body, continuous_medication_required_calculation
):
    """
    Test the history of continuous medication required algorithm

    :param request_body: request body with blood pressure readings and other data
    :type request_body: dict
    :param continuous_medication_required_calculation: correct return value from algorithm
    :type continuous_medication_required_calculation: dict
    """
    assert (
        continuous_medication.continuous_medication_required(request_body)
        == continuous_medication_required_calculation
    )


@pytest.mark.parametrize(
    "request_body, mas_medication_calculation",
    [
        # Medication used to treat hypertension
        (
                {
                    "evidence": {
                        "bp_readings": [],
                        "medications": [
                            {
                                "description": "Benazepril",
                                "status": "active",
                                "authoredOn": "2020-04-06T04:00:00Z",
                                "dataSource": "MAS",
                                "receiptDate": "",
                            }
                        ]
                    },
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                    "disabilityActionType": "INCREASE"
                },
                {
                    "medications": [
                        {
                            "authoredOn": "2020-04-06T04:00:00Z",
                            "dateFormatted": "4/6/2020",
                            "description": "Benazepril",
                            "status": "active",
                            "receiptDate": "",
                            "dataSource": "MAS"
                        }
                    ],
                    "medicationsCount": 1,
                },
        ),
        # Medication used to treat hypertension
        (
                {
                    "evidence": {
                        "bp_readings": [],
                        "medications": [
                            {
                                "description": "Benazepril",
                                "status": "active",
                                "authoredOn": "2020-04-06T04:00:00Z",
                                "dataSource": "MAS",
                                "receiptDate": "",
                            }
                        ]
                    },
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                    "disabilityActionType": "INCREASE"
                },
                {
                    "medications": [
                        {
                            "authoredOn": "2020-04-06T04:00:00Z",
                            "dateFormatted": "4/6/2020",
                            "description": "Benazepril",
                            "status": "active",
                            "receiptDate": "",
                            "dataSource": "MAS"
                        }
                    ],
                    "medicationsCount": 1,
                },
        ),
        # Medication not used to treat hypertension
        (
                {
                    "evidence": {
                        "bp_readings": [],
                        "medications": [
                            {
                                "description": "Advil",
                                "status": "active",
                                "authoredOn": "1950-04-06T04:00:00Z",
                                "dataSource": "MAS",
                                "receiptDate": "",

                            }
                        ]
                    },
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                    "disabilityActionType": "INCREASE"
                },
                {
                    "medications": [],
                    "medicationsCount": 0
                },
        ),
        (
                {
                    "evidence": {
                        "bp_readings": [],
                        "medications": [
                            {
                                "description": "Benazepril",
                                "status": "active",
                                "authoredOn": "",
                                "receiptDate": "",
                                "dataSource": "MAS"
                            },
                            {
                                "description": "Advil",
                                "status": "active",
                                "receiptDate": "",
                                "authoredOn": "2021-04-06T04:00:00Z",
                                "dataSource": "LH"
                            },
                            {
                                "description": "some medication",
                                "status": "active",
                                "authoredOn": "",
                                "receiptDate": "",
                                "partialDate": "**/**/1988",
                                "dataSource": "MAS"
                            },
                        ],
                    },
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                    "disabilityActionType": "NEW"
                },
                {
                    "medications": [
                        {
                            "description": "Benazepril",
                            "status": "active",
                            "dateFormatted": "",
                            "receiptDate": "",
                            "authoredOn": "",
                            "dataSource": "MAS"
                        },
                        {
                            "description": "some medication",
                            "status": "active",
                            "authoredOn": "",
                            "receiptDate": "",
                            "dateFormatted": "",
                            "partialDate": "**/**/1988",
                            "dataSource": "MAS"
                        },
                    ],
                    "medicationsCount": 2,
                },
        ),
        (
                {
                    "evidence": {
                        "bp_readings": [],
                        "medications": [],
                    },
                    "claimSubmissionDateTime": "2021-11-09T04:00:00Z",
                    "disabilityActionType": "INCREASE"
                },
                {"medications": [], "medicationsCount": 0},
        ),
    ],
)
def test_filter_mas_medication(request_body, mas_medication_calculation):

    assert (
            continuous_medication.filter_mas_medication(request_body)
            == mas_medication_calculation
    )
