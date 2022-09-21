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
                        }
                    ],
                    "date_of_claim": "2021-11-09",
                }
            },
            {'medications': [{'authoredOn': '1950-04-06T04:00:00Z',
                              'conditionRelated': 'true',
                              'description': 'Albuterol inhaler',
                              'status': 'active',
                              'suggestedCategory': ['Bronchodilator/Used in Respiratory '
                                                    'Failure']}],
             'relevantMedCount': 1,
             'totalMedCount': 1},
        ),
        # Not service connected but uses medication used to treat hypertension
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "Albuterol",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        }
                    ],
                    "date_of_claim": "2021-11-09",
                }
            },
            {'medications': [{'authoredOn': '1950-04-06T04:00:00Z',
                              'conditionRelated': 'true',
                              'description': 'Albuterol',
                              'status': 'active',
                              'suggestedCategory': ['Bronchodilator/Used in Respiratory '
                                                    'Failure']}],
             'relevantMedCount': 1,
             'totalMedCount': 1},
        ),
        # Service connected but doesn't use medication used to treat hypertension
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "Advil",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        }
                    ],
                    "date_of_claim": "2021-11-09",
                }
            },
            {'medications': [{'authoredOn': '1950-04-06T04:00:00Z',
                              'conditionRelated': 'false',
                              'description': 'Advil',
                              'status': 'active',
                              'suggestedCategory': []}],
             'relevantMedCount': 0,
             'totalMedCount': 1},
        ),
        # multiple medications, some to treat and others not to treat asthma
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "Albuterol",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        },
                        {
                            "description": "Advil",
                            "status": "active",
                            "authoredOn": "1952-04-06T04:00:00Z",
                        },
                    ],
                    "date_of_claim": "2021-11-09",
                }
            },
            {'medications': [{'authoredOn': '1950-04-06T04:00:00Z',
                              'conditionRelated': 'true',
                              'description': 'Albuterol',
                              'status': 'active',
                              'suggestedCategory': ['Bronchodilator/Used in Respiratory '
                                                    'Failure']},
                             {'authoredOn': '1952-04-06T04:00:00Z',
                              'conditionRelated': 'false',
                              'description': 'Advil',
                              'status': 'active',
                              'suggestedCategory': []}],
             'relevantMedCount': 1,
             'totalMedCount': 2},
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
                            "authoredOn": "1950-04-06T04:00:00Z",
                        },
                    ],
                    "date_of_claim": "2021-11-09",
                }
            },
            {'medications': [{'authoredOn': '1950-04-06T04:00:00Z',
                              'conditionRelated': 'true',
                              'description': '14 ACTUAT fluticasone furoate 0.1 MG/ACTUAT '
                                             '/ vilanterol 0.025 MG/ACTUAT Dry Powder '
                                             'Inhaler',
                              'status': 'active',
                              'suggestedCategory': ['Anti-Inflammatory/Bronchodilator/Corticosteroid/Immuno-Suppressive']}],
             'relevantMedCount': 1,
             'totalMedCount': 1}
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
        medication.medication_required(request_body)
        == continuous_medication_required_calculation
    )


@pytest.mark.parametrize(
    "medication_display, expected",
    [
        ("Albuterol", ["Bronchodilator/Used in Respiratory Failure"]),
        ("Advil", []),
        # medication description contains multiple keywords,
        # returns the most general category for any medication in description
        (
            "14 ACTUAT fluticasone furoate 0.1 MG/ACTUAT / vilanterol 0.025 MG/ACTUAT Dry Powder Inhaler",
            ["Anti-Inflammatory/Bronchodilator/Corticosteroid/Immuno-Suppressive"],
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

    category = medication.categorize_med(medication_display)

    assert category == expected
