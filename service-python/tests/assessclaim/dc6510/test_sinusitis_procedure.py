import pytest
from assessclaimdc6510.src.lib import procedure


@pytest.mark.parametrize(
    "request_body, filtered_procedures",
    [
        (
                {
                    "evidence": {
                        "procedures": [
                            {"text": "Nasal/sinus endoscopy, surgical, with dilation (eg, balloon dilation); sphenoid "
                                     "sinus ostium",
                             "code": "31297",
                             "status": "completed",
                             "performedDate": "2010-03-22"},
                            {"text": "Anesthesia for procedures on nose and accessory sinuses; radical surgery",
                             "code": "00162",
                             "status": "completed",
                             "performedDate": "2010-2-22"}
                        ],
                        "dateOfClaim": "2021-11-09",
                    }
                },
                {
                    "procedures": [
                        {"text": "Nasal/sinus endoscopy, surgical, with dilation (eg, balloon dilation); sphenoid "
                                 "sinus ostium",
                         "code": "31297",
                         "status": "completed",
                         "performedDate": "2010-03-22"},
                        {"text": "Anesthesia for procedures on nose and accessory sinuses; radical surgery",
                         "code": "00162",
                         "status": "completed",
                         "performedDate": "2010-2-22"}
                    ],
                    "relevantProceduresCount": 2,
                    "totalProceduresCount": 2,
                    "radicalSurgery": True,
                    "multipleSurgery": False
                },
        ),
        (
                {
                    "evidence": {
                        "procedures": [
                            {"text": "Nasal/sinus endoscopy, surgical, with dilation (eg, balloon dilation); sphenoid "
                                     "sinus ostium",
                             "code": "31297",
                             "status": "completed",
                             "performedDate": "2010-2-22"},
                            {"text": "Thoracoscopy, surgical; with partial pulmonary decortication",
                             "code": "32651",
                             "status": "completed",
                             "performedDate": "2010-2-22"}
                        ],
                        "dateOfClaim": "2021-11-09",
                    }
                },
                {
                    "procedures": [
                        {"text": "Nasal/sinus endoscopy, surgical, with dilation (eg, balloon dilation); sphenoid "
                                 "sinus ostium",
                         "code": "31297",
                         "status": "completed",
                         "performedDate": "2010-2-22"}
                    ],
                    "radicalSurgery": False,
                    "relevantProceduresCount": 1,
                    "totalProceduresCount": 2,
                    "multipleSurgery": False
                },
        ),
    ],
)
def test_procedure(
        request_body, filtered_procedures
):
    """
    Test the condition filtering algorithm

    :param request_body: request body with blood pressure readings and other data
    :type request_body: dict
    :param filtered_procedures: correct return value from algorithm
    :type filtered_procedures: dict
    """
    assert (
            procedure.procedures_calculation(request_body)
            == filtered_procedures
    )
