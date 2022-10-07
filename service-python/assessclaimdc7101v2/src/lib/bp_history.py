def history_of_diastolic_bp(request_body):
    """
    Determine if the Veteran has "a history of diastolic pressure predominantly 100 or more"

    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """

    calculation = {
        "success": True
    }

    bp_readings = request_body["evidence"]["bp_readings"]
    bp_readings_length = len(bp_readings)
    diastolic_history_calculation = {"totalBpReadings": bp_readings_length}
    readings_greater_or_equal_to_one_hundred = 0

    if bp_readings_length > 0:
        for reading in bp_readings:
            if reading["diastolic"]["value"] >= 100:
                readings_greater_or_equal_to_one_hundred += 1
        calculation["diastolic_bp_predominantly_100_or_more"] = True if readings_greater_or_equal_to_one_hundred / bp_readings_length >=.5 else False
    else:
        calculation["success"] = False

    diastolic_history_calculation["calculated"] = calculation
    return diastolic_history_calculation
