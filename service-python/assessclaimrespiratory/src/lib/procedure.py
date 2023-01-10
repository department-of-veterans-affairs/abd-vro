from datetime import datetime

from .codesets import procedures_codeset


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
    resp_procedure = []

    procedures = request_body["evidence"]["procedures"]
    procedures_count = len(procedures)
    for procedure in procedures:
        if procedure["status"].lower() in ["in-progress", "on-hold", "stopped", "completed"]:
            procedure_code = procedure["code"]
            for category_id in list(procedures_codeset.resp_procedures.keys()):
                if procedure_code in procedures_codeset.resp_procedures[category_id]:
                    resp_procedure.append(category_id)
                    relevant_procedures.append(procedure)
                    break

    relevant_procedures = sorted(
        relevant_procedures,
        key=lambda i: datetime.strptime(i["performedDate"], "%Y-%m-%d").date(),
        reverse=True,
    )

    response.update({
        "procedures": relevant_procedures,
        "relevantProceduresCount": len(relevant_procedures),
        "totalProceduresCount": procedures_count,
        "respProcedures": resp_procedure
    })

    return response
