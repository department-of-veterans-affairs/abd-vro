from datetime import datetime


def procedure_match(request_body):
    """
    Determine if there is the veteran requires continuous medication for pancreatic cancer

    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """
    procedure_match_calculation = {
        "success": True,
        "procedure_within_six_months": False
    }

    date_of_claim = request_body["date_of_claim"]
    date_of_claim_datetime = datetime.strptime(date_of_claim, "%Y-%m-%d").date()

    for procedure in request_body["procedure"]:
        code = procedure["code"]
        status = procedure["status"].lower()
        if code in pc_procedure_cpt and status == "active":
            procedure_match_calculation["procedure_within_six_months"] = True
        if code in pc_procedure_cpt and status != "active":
            procedure_date = procedure["performed_date"]
            procedure_date_formatted = datetime.strptime(procedure_date, "%Y-%m-%d").date()
            if (date_of_claim_datetime - procedure_date_formatted).days <= 180:
                procedure_match_calculation["procedure_within_six_months"] = True

    return procedure_match_calculation
