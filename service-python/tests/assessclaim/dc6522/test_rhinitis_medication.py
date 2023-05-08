import pytest
from assessclaimdc6522.src.lib import medication


@pytest.mark.parametrize(
    "request_body, continuous_medication_required_calculation",
    [
        # Service connected and medication used to treat hypertension
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "fexofenadine Oral Tablet [Allegra]",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        }
                    ],
                    "date_of_claim": "2021-11-09",
                }
            },
            {
                "medications": [
                    {
                        "conditionRelated": True,
                        "authoredOn": "1950-04-06T04:00:00Z",
                        "description": "fexofenadine Oral Tablet [Allegra]",
                        "status": "active",
                        "suggestedCategory": "Antihistimine"
                    }
                ],
                "relevantMedCount": 1,
                "totalMedCount": 1,
            },
        ),
        # Not service connected but uses medication used to treat hypertension
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "pseudoephedrine 45 MG",
                            "status": "active",
                            "authoredOn": "1950-04-06T04:00:00Z",
                        }
                    ],
                    "date_of_claim": "2021-11-09",
                }
            },
            {
                "medications": [
                    {
                        "conditionRelated": True,
                        "authoredOn": "1950-04-06T04:00:00Z",
                        "description": "pseudoephedrine 45 MG",
                        "status": "active",
                        "suggestedCategory": "Antihistamine / Decongestant"
                    }
                ],
                "relevantMedCount": 1,
                "totalMedCount": 1,
            },
        ),
        # Service connected but doesn"t use medication used to treat hypertension
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
            {
                "medications": [
                    {
                        "conditionRelated": False,
                        "authoredOn": "1950-04-06T04:00:00Z",
                        "description": "Advil",
                        "status": "active",
                    }
                ],
                "relevantMedCount": 0,
                "totalMedCount": 1,
            },
        ),
        # multiple medications, some to treat and others not to treat asthma
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "diphenhydrAMINE hydrochloride 2.5 MG/ML Oral Solution",
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
            {
                "medications": [
                    {
                        "conditionRelated": True,
                        "authoredOn": "1950-04-06T04:00:00Z",
                        "description": "diphenhydrAMINE hydrochloride 2.5 MG/ML Oral Solution",
                        "status": "active",
                        "suggestedCategory": "Antihistimine"
                    },
                    {
                        "conditionRelated": False,
                        "authoredOn": "1952-04-06T04:00:00Z",
                        "description": "Advil",
                        "status": "active",
                    },
                ],
                "relevantMedCount": 1,
                "totalMedCount": 2,
            },
        ),
    ],
)
def test_medication(
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
