import pytest
from assessclaimdc6522.src.lib import procedure


@pytest.mark.parametrize(
    "request_body, continuous_medication_required_calculation",
    [
        # Service connected and medication used to treat hypertension
        (
                {
                    "evidence": {
                        "procedures": [
                            {
                                "text": "Professional services for allergen immunotherapy not including "
                                        "provision of allergenic extracts; two or more injections",
                                "code": "95117",
                                "status": "completed",
                                "performedDate": "1950-04-06",
                            }
                        ],
                        "date_of_claim": "2021-11-09",
                    }
                },
                {
                    "procedures": [
                        {
                            "text": "Professional services for allergen immunotherapy not including "
                                    "provision of allergenic extracts; two or more injections",
                            "code": "95117",
                            "status": "completed",
                            "performedDate": "1950-04-06",
                        }
                    ],
                    "relevantProceduresCount": 1,
                    "totalProceduresCount": 1,
                },
        ),
        (
                {
                    "evidence": {
                        "procedures": [
                            {
                                "text": "Documentation of current medications",
                                "code": "XXXXX",
                                "status": "Completed",
                                "performedDate": "2009-03-19",
                                "codeSystem": "http://www.ama-assn.org/go/cpt"
                            }
                        ],
                        "date_of_claim": "2021-11-09",
                    }
                },
                {
                    "procedures": [
                    ],
                    "relevantProceduresCount": 0,
                    "totalProceduresCount": 1,
                },


        ),
    ],
)
def test_procedures(
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
            procedure.procedures_calculation(request_body)
            == continuous_medication_required_calculation
    )
