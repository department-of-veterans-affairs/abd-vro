from datetime import datetime

from .codesets import emphysema_procedure_codesets, bronchitis_procedures_codeset, copd_procedures_codesets


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

    procedures = request_body["evidence"]["procedures"]
    procedures_count = len(procedures)
    for procedure in procedures:
        if procedure["status"].lower() in ["in-progress", "on-hold", "stopped", "completed"]:
            procedure_code = procedure["code"]
            if procedure_code in bronchitis_procedures_codeset.bronchitis_procedures:
                relevant_procedures.append(procedure)
            if procedure_code in copd_procedures_codesets.copd_procedures:
                relevant_procedures.append(procedure)
            if procedure_code in emphysema_procedure_codesets.procedures_cpt:
                relevant_procedures.append(procedure)

    relevant_procedures = sorted(
        relevant_procedures,
        key=lambda i: datetime.strptime(i["performedDate"], "%Y-%m-%d").date(),
        reverse=True,
    )

    response.update({
        "procedures": relevant_procedures,
        "relevantProceduresCount": len(relevant_procedures),
        "totalProceduresCount": procedures_count,
    })

    return response
