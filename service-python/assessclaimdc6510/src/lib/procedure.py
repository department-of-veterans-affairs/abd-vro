from datetime import datetime

from .codesets import procedure_codesets


def procedures_calculation(request_body):
    """
    Determine if there is the veteran requires continuous medication for hypertension
    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """
    response = {}
    relevant_procedures = []
    radical_procedures = []
    radical_surgery_procedure = False
    multiple_surgery = False

    veterans_procedures = request_body["evidence"]["procedures"]
    procedures_count = len(veterans_procedures)
    for procedure in veterans_procedures:
        if procedure["status"].lower() in ["in-progress", "on-hold", "stopped", "completed"]:
            procedure_code = procedure["code"]
            if procedure_code in procedure_codesets.surgery:
                relevant_procedures.append(procedure)
            elif procedure_code in procedure_codesets.radical_surgery:
                radical_surgery_procedure = True
                radical_procedures.append(procedure)

    if len(relevant_procedures) >= 2:
        multiple_surgery = True

    relevant_procedures.extend(radical_procedures)

    relevant_procedures = sorted(
        relevant_procedures,
        key=lambda i: datetime.strptime(i["performedDate"], "%Y-%m-%d").date(),
        reverse=True,
    )

    response.update({
        "procedures": relevant_procedures,
        "relevantProceduresCount": len(relevant_procedures),
        "totalProceduresCount": procedures_count,
        "radicalSurgery": radical_surgery_procedure,
        "multipleSurgery": multiple_surgery
    })

    return response
