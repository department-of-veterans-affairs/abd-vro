from .codesets import procedure_codesets


def procedures_calculation(request_body):
    """
    Determine if there is the veteran requires continuous medication for hypertension
    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """
    relevant_procedures = []

    veterans_procedures = request_body["evidence"]["procedure"]
    for procedure in veterans_procedures:
        if procedure["status"].lower() in ["in-progress", "on-hold", "stopped", "completed"]:
            procedure_code = procedure["code"]
            if procedure_code in procedure_codesets.surgery:
                relevant_procedures.append(procedure)

    return relevant_procedures
