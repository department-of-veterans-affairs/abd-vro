import pytest

from assessclaimdc6602.src.lib import medication


@pytest.mark.parametrize(
    "request_body, continuous_medication_required_calculation",
    [
        # Service connected and medication used to treat hypertension
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "Albuterol inhaler",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                            "dataSource": "MAS"
                        }
                    ],
                },
                "claimSubmissionDateTime": "2021-11-09",
            },
            {'allMedications': [{'authoredOn': '1950-04-06T04:00:00Z',
                                 'classification': 'Bronchodilator/Used in Respiratory '
                                                   'Failure',
                                 'dataSource': 'MAS',
                                 'dateFormatted': '4/6/1950',
                                 'description': 'Albuterol inhaler',
                                 'receiptDate': '',
                                 'status': 'active'}],
             'allMedicationsCount': 1,
             'twoYearsMedications': [],
             'twoYearsMedicationsCount': 0,
             'relevantMedicationCount': 1}
        ),
        # Not service connected but uses medication used to treat hypertension
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "Albuterol",
                            "dataSource": "MAS",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        }
                    ]
                },
                "claimSubmissionDateTime": "2021-11-09",
            },
            {'allMedications': [{'authoredOn': '1950-04-06T04:00:00Z',
                                 'classification': 'Bronchodilator/Used in Respiratory '
                                                   'Failure',
                                 'dataSource': 'MAS',
                                 'dateFormatted': '4/6/1950',
                                 'description': 'Albuterol',
                                 'receiptDate': '',
                                 'status': 'active'}],
             'allMedicationsCount': 1,
             'twoYearsMedications': [],
             'twoYearsMedicationsCount': 0,
             'relevantMedicationCount': 1}
        ),
        # Service connected but doesn't use medication used to treat hypertension
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "Advil",
                            "status": "active",
                            "dataSource": "MAS",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        }
                    ],
                },
                "claimSubmissionDateTime": "2021-11-09",
            },
            {'allMedications': [{'authoredOn': '1950-04-06T04:00:00Z',
                                 'classification': '',
                                 'dataSource': 'MAS',
                                 'dateFormatted': '4/6/1950',
                                 'description': 'Advil',
                                 'receiptDate': '',
                                 'status': 'active'}],
             'allMedicationsCount': 1,
             'twoYearsMedications': [],
             'twoYearsMedicationsCount': 0,
             'relevantMedicationCount': 0}
        ),
        # multiple medications, some to treat and others not to treat asthma
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "Albuterol",
                            "status": "active",
                            "dataSource": "MAS",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        },
                        {
                            "description": "Advil",
                            "status": "active",
                            "dataSource": "MAS",
                            "authoredOn": "1952-04-06T04:00:00Z",
                        },
                    ]
                },
                "claimSubmissionDateTime": "2021-11-09",
            },
            {'allMedications': [{'authoredOn': '1952-04-06T04:00:00Z',
                                 'classification': '',
                                 'dataSource': 'MAS',
                                 'dateFormatted': '4/6/1952',
                                 'description': 'Advil',
                                 'receiptDate': '',
                                 'status': 'active'},
                                {'authoredOn': '1950-04-06T04:00:00Z',
                                 'classification': 'Bronchodilator/Used in Respiratory '
                                                   'Failure',
                                 'dataSource': 'MAS',
                                 'dateFormatted': '4/6/1950',
                                 'description': 'Albuterol',
                                 'receiptDate': '',
                                 'status': 'active'}],
             'allMedicationsCount': 2,
             'twoYearsMedications': [],
             'twoYearsMedicationsCount': 0,
             'relevantMedicationCount': 1}
        ),
        # medication description contains multiple keywords
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "14 ACTUAT fluticasone furoate 0.1 MG/ACTUAT / "
                            "vilanterol 0.025 MG/ACTUAT Dry Powder Inhaler",
                            "status": "active",
                            "dataSource": "MAS",
                            "authoredOn": "2021-04-06T04:00:00Z",
                        }
                    ]
                },
                "claimSubmissionDateTime": "2021-11-09",

            },
            {'allMedications': [{'authoredOn': '2021-04-06T04:00:00Z',
                                 'classification': 'Anti-Inflammatory/Bronchodilator/Corticosteroid/Immuno-Suppressive',
                                 'dataSource': 'MAS',
                                 'dateFormatted': '4/6/2021',
                                 'description': '14 ACTUAT fluticasone furoate 0.1 '
                                                'MG/ACTUAT / vilanterol 0.025 MG/ACTUAT '
                                                'Dry Powder Inhaler',
                                 'receiptDate': '',
                                 'status': 'active'}],
             'allMedicationsCount': 1,
             'twoYearsMedications': [],
             'twoYearsMedicationsCount': 0,
             'relevantMedicationCount': 1}
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
        medication.filter_categorize_mas_medication(request_body)
        == continuous_medication_required_calculation
    )


@pytest.mark.parametrize(
    "medication_display, expected",
    [
        ("Albuterol", "Bronchodilator/Used in Respiratory Failure"),
        ("Advil", ''),
        # medication description contains multiple keywords,
        # returns the most general category for any medication in description
        (
            "14 ACTUAT fluticasone furoate 0.1 MG/ACTUAT / vilanterol 0.025 MG/ACTUAT Dry Powder Inhaler",
            "Anti-Inflammatory/Bronchodilator/Corticosteroid/Immuno-Suppressive",
        ),
    ],
)
# Service connected and medication used to treat hypertension
def test_categorize_med(medication_display, expected):
    """
    Test the categorization of medications
    :param medication_display: medication description
    :param expected: category
    """

    category = medication.classify_med(medication_display)

    assert category == expected
