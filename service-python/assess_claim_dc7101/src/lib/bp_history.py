def history_of_diastolic_bp(request_body):
    """
    Determine if the Veteran has "a history of diastolic pressure predominantly 100 or more"

    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """

    diastolic_history_calculation = {
        "success": True
    }
    bp_readings = request_body["observation"]["bp"]
    bp_readings_length = len(bp_readings)
    readings_greater_or_equal_to_one_hundred = 0

    if bp_readings_length > 0:
        for reading in bp_readings:
            if reading["diastolic"] >= 100:
                readings_greater_or_equal_to_one_hundred += 1
        diastolic_history_calculation["diastolic_bp_predominantly_100_or_more"] = True if readings_greater_or_equal_to_one_hundred / bp_readings_length >=.5 else False
    else:
        diastolic_history_calculation["success"] = False

    return diastolic_history_calculation