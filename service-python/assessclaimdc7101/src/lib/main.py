import logging
from typing import Dict
from datetime import date

from . import continuous_medication, utils, bp_calculator, conditions


def assess_hypertension(event: Dict):
    """
    Take a request that includes hypertension related data, and return a response

    :param event: request body
    :type event: dict
    :return: response body
    :rtype: dict
    """

    validation_results = utils.validate_request_body(event)
    response_body = {}

    if "dateOfClaim" not in event:
        event["dateOfClaim"] = str(date.today())

    if validation_results["is_valid"]:
        relevant_medication = continuous_medication.continuous_medication_required(
            event
        )
        bp_readings = bp_calculator.bp_reader(event)
        response_body.update(
            {
                "evidence": {
                    "medications": relevant_medication["medications"],
                    "bp_readings": bp_readings["oneYearBp"],
                },
                "evidenceSummary": {
                    "totalBpReadings": bp_readings["totalBpReadings"],
                    "recentBpReadings": bp_readings["oneYearBpReadings"],
                    "medicationsCount": relevant_medication["medicationsCount"],
                },
            }
        )
        logging.info("Message processed successfully")
    else:
        logging.info(f"Message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"

    return response_body


def assess_sufficiency(event: Dict):
    """
    Take a request that includes hypertension related data, and return a response

    :param event: request body
    :type event: dict
    :return: response body
    :rtype: dict
    """

    validation_results = utils.validate_request_body(event)
    response_body = {}
    if "dateOfClaim" not in event:
        event["dateOfClaim"] = str(date.today())

    if validation_results["is_valid"] and "disabilityActionType" in event:
        bp_calculation = bp_calculator.bp_reader(event)
        relevant_conditions = conditions.conditions_calculation(event)

        sufficient = None
        if event["disabilityActionType"] == "INCREASE":
            if bp_calculation["oneYearBpReadings"] >= 4:
                sufficient = True
        if event["disabilityActionType"] == "NEW":
            if relevant_conditions["conditions"]:
                sufficient = False
                if bp_calculation["twoYearsBpReadings"] >= 2:
                    sufficient = True
            if bp_calculation["recentElevatedBpReadings"] >= 2:
                sufficient = True

        response_body.update(
            {
                "evidence": {
                    "bp_readings": bp_calculation["twoYearsBp"],
                    "conditions": relevant_conditions["conditions"]
                },
                "evidenceSummary": {
                    "totalBpReadings": bp_calculation["totalBpReadings"],
                    "recentBpReadings": bp_calculation["twoYearsBpReadings"],
                    "relevantConditionsCount": relevant_conditions["relevantConditionsCount"],
                    "totalConditionsCount": relevant_conditions["totalConditionsCount"]
                },
                "sufficientForFastTracking": sufficient,
                "dateOfClaim": event["dateOfClaim"],
                "disabilityActionType": event["disabilityActionType"],
            })
        logging.info("Message processed successfully")
    else:
        logging.info(f"Message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"

    return response_body
