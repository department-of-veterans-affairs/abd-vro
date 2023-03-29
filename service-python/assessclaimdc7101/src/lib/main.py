import logging
from datetime import date
from typing import Dict

from . import bp_calculator, conditions, medications, utils


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
        relevant_medication = medications.medication_required(
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
                    "totalBpCount": bp_readings["totalBpCount"],
                    "recentBpCount": bp_readings["oneYearBpCount"],
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
        relevant_medications = medications.filter_mas_medication(event)
        bp_display = bp_calculation["twoYearsBp"]
        conditions_display = relevant_conditions["twoYearsConditions"]

        sufficient = None
        if event["disabilityActionType"] == "INCREASE":
            if bp_calculation["oneYearBpCount"] >= 3:
                sufficient = True
            else:
                sufficient = False
        if event["disabilityActionType"] == "NEW":
            bp_display = bp_calculation["allBp"]  # Include all bp readings to display
            conditions_display = relevant_conditions["conditions"]
            if relevant_conditions["relevantConditionsLighthouseCount"] >= 1:
                if bp_calculation["twoYearsBpCount"] >= 3:
                    sufficient = True
                else:
                    sufficient = False
            if bp_calculation["twoYearsElevatedBpCount"] >= 1 and bp_calculation["twoYearsBpCount"] >= 3:
                sufficient = True

        response_body.update(
            {
                "evidence": {
                    "bp_readings": bp_display,
                    "conditions": conditions_display,
                    "medications": relevant_medications["medications"],
                    "documentsWithoutAnnotationsChecked": utils.docs_without_annotations_ids(event)
                },
                "evidenceSummary": {
                    "totalBpCount": bp_calculation["totalBpCount"],
                    "twoYearsBpCount": bp_calculation["twoYearsBpCount"],
                    "oneYearBpCount": bp_calculation["oneYearBpCount"],
                    "twoYearsElevatedBpCount": bp_calculation["twoYearsElevatedBpCount"],
                    "relevantConditionsLighthouseCount": relevant_conditions["relevantConditionsLighthouseCount"],
                    "totalConditionsCount": relevant_conditions["totalConditionsCount"],
                    "medicationsCount": relevant_medications["medicationsCount"]
                },
                "sufficientForFastTracking": sufficient,
                "claimSubmissionDateTime": event["claimSubmissionDateTime"],
                "disabilityActionType": event["disabilityActionType"],
                "claimSubmissionId": event['claimSubmissionId']
            })
        logging.info(f"claimSubmissionId: {event['claimSubmissionId']}, sufficientForFastTracking: {sufficient}, "
                     f"evidenceSummary: {response_body['evidenceSummary']}")
    else:
        logging.info(
            f"claimSubmissionId: {event['claimSubmissionId']}, message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"
        response_body["claimSubmissionId"] = event['claimSubmissionId']

    return response_body
