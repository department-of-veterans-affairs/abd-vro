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
    radical_surgery_procedure = []

    veterans_procedures = request_body["evidence"]["procedures"]
    procedures_count = len(veterans_procedures)
    for procedure in veterans_procedures:
        if procedure["status"].lower() in ["in-progress", "on-hold", "stopped", "completed"]:
            procedure_text = procedure["text"]
            if procedure_text in procedure_codesets.surgery:
                relevant_procedures.append(procedure)
            elif procedure_text in procedure_codesets.radical_surgery:
                radical_surgery_procedure.append(procedure)
                relevant_procedures.append(procedure)

    # sort by date
        relevant_procedures = sorted(
            relevant_procedures,
            key=lambda i: datetime.strptime(i["performedDate"], "%Y-%m-%d").date(),
            reverse=True,
        )
    # put radical surgery first

    response["procedures"] = relevant_procedures
    response["relevantProceduresCount"] = len(relevant_procedures)
    response["totalProceduresCount"] = procedures_count

    return response
