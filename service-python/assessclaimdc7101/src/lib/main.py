import logging
from datetime import date
from typing import Dict

from . import bp_calculator, conditions, continuous_medication, utils


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

    if "claimSubmissionDateTime" not in event:
        event["claimSubmissionDateTime"] = str(f"{date.today()}T00:00:00Z")

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
                "claimSubmissionId": event['claimSubmissionId']
            }
        )
        logging.info(f"claimSubmissionId: {event['claimSubmissionId']}, message processed successfully")
    else:
        logging.info(
            f"claimSubmissionId: {event['claimSubmissionId']}, message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"
        response_body["claimSubmissionId"] = event['claimSubmissionId']

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
    event = validation_results["request_body"]
    response_body = {}
    if "claimSubmissionDateTime" not in event:
        event["claimSubmissionDateTime"] = str(f"{date.today()}T04:00:00Z")

    if validation_results["is_valid"] and "disabilityActionType" in event:
        bp_calculation = bp_calculator.bp_reader(event)
        relevant_conditions = conditions.conditions_calculation(event)
        bp_display = bp_calculation["twoYearsBp"]
        conditions_display = relevant_conditions["conditionsTwoYears"]
        total_relevant_conditions = relevant_conditions["relevantConditionsCountMAS"] + relevant_conditions["relevantConditionsCountLighthouse"]

        sufficient = None
        if event["disabilityActionType"] == "INCREASE":
            if bp_calculation["oneYearBpReadings"] >= 3:
                sufficient = True
        if event["disabilityActionType"] == "NEW":
            bp_display = bp_calculation["allBp"]  # Include all bp readings to display
            conditions_display = relevant_conditions["conditions"]
            if total_relevant_conditions >= 1:
                sufficient = False
                if bp_calculation["twoYearsBpReadings"] >= 3:
                    sufficient = True
            if bp_calculation["recentElevatedBpReadings"] >= 1 and bp_calculation["twoYearsBpReadings"] >= 3:
                sufficient = True

        response_body.update(
            {
                "evidence": {
                    "bp_readings": bp_display,
                    "conditions": conditions_display
                },
                "evidenceSummary": {
                    "totalBpReadings": bp_calculation["totalBpReadings"],
                    "recentBpReadings": bp_calculation["twoYearsBpReadings"],
                    "relevantConditionsCountLighthouse": relevant_conditions["relevantConditionsCountLighthouse"],
                    "relevantConditionsCountMAS": relevant_conditions["relevantConditionsCountMAS"],
                    "irrelevantConditionsCountMAS": relevant_conditions["irrelevantConditionsCountMAS"],
                    "totalConditionsCount": relevant_conditions["totalConditionsCount"]
                },
                "sufficientForFastTracking": sufficient,
                "claimSubmissionDateTime": event["claimSubmissionDateTime"],
                "disabilityActionType": event["disabilityActionType"],
                "claimSubmissionId": event['claimSubmissionId']
            })
        if "medications" in event["evidence"].keys():
            medications = continuous_medication.filter_mas_medication(event)
            response_body["evidence"].update(
                {
                    "medications": medications["medications"]
                }
            )
            response_body["evidenceSummary"].update(
                {
                    "medicationsCount": medications["medicationsCount"]
                }
            )
        logging.info(f"claimSubmissionId: {event['claimSubmissionId']}, sufficientForFastTracking: {sufficient}, "
                     f"evidenceSummary: {response_body['evidenceSummary']}")
    else:
        logging.info(
            f"claimSubmissionId: {event['claimSubmissionId']}, message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"
        response_body["claimSubmissionId"] = event['claimSubmissionId']

    return response_body
