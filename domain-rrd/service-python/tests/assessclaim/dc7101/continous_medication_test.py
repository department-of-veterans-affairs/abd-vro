import pytest
from assessclaimdc7101.src.lib import medications


@pytest.mark.parametrize(
    "request_body, medication_required_calculation",
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
def test_medication_required(
    request_body, medication_required_calculation
):
    """
    Test the medication algorithm

    :param request_body: request body with medications
    :type request_body: dict
    :param medication_required_calculation: correct return value from algorithm
    :type medication_required_calculation: dict
    """
    assert (
        medications.medication_required(request_body)
        == medication_required_calculation
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
                {'allMedications': [{'authoredOn': '2020-04-06T04:00:00Z',
                                     'dataSource': 'MAS',
                                     'dateFormatted': '4/6/2020',
                                     'description': 'Benazepril',
                                     'receiptDate': '',
                                     'status': 'active'}],
                 'allMedicationsCount': 1,
                 'twoYearsMedications': [{'authoredOn': '2020-04-06T04:00:00Z',
                                          'dataSource': 'MAS',
                                          'dateFormatted': '4/6/2020',
                                          'description': 'Benazepril',
                                          'receiptDate': '',
                                          'status': 'active'}],
                 'twoYearsMedicationsCount': 1},
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
                {'allMedications': [{'authoredOn': '2020-04-06T04:00:00Z',
                                     'dataSource': 'MAS',
                                     'dateFormatted': '4/6/2020',
                                     'description': 'Benazepril',
                                     'receiptDate': '',
                                     'status': 'active'}],
                 'allMedicationsCount': 1,
                 'twoYearsMedications': [{'authoredOn': '2020-04-06T04:00:00Z',
                                          'dataSource': 'MAS',
                                          'dateFormatted': '4/6/2020',
                                          'description': 'Benazepril',
                                          'receiptDate': '',
                                          'status': 'active'}],
                 'twoYearsMedicationsCount': 1}
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
                {'allMedications': [{'authoredOn': '1950-04-06T04:00:00Z',
                                     'dataSource': 'MAS',
                                     'dateFormatted': '4/6/1950',
                                     'description': 'Advil',
                                     'receiptDate': '',
                                     'status': 'active'}],
                 'allMedicationsCount': 1,
                 'twoYearsMedications': [],
                 'twoYearsMedicationsCount': 0}
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
                {'allMedications': [{'authoredOn': '',
                                     'dataSource': 'MAS',
                                     'dateFormatted': '',
                                     'description': 'Benazepril',
                                     'receiptDate': '',
                                     'status': 'active'},
                                    {'authoredOn': '',
                                     'dataSource': 'MAS',
                                     'dateFormatted': '',
                                     'description': 'some medication',
                                     'partialDate': '**/**/1988',
                                     'receiptDate': '',
                                     'status': 'active'}],
                 'allMedicationsCount': 3,
                 'twoYearsMedications': [],
                 'twoYearsMedicationsCount': 0}
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
                {'allMedications': [],
                 'allMedicationsCount': 0,
                 'twoYearsMedications': [],
                 'twoYearsMedicationsCount': 0}
        ),
    ],
)
def test_filter_mas_medication(request_body, mas_medication_calculation):

    assert (
            medications.filter_mas_medication(request_body)
            == mas_medication_calculation
    )
