import logging
from typing import Dict

import data_model
import utils

from . import condition, medication


def assess_asthma(event: Dict):
    """
    Take a request that includes asthma related data, and return a filtered response

    :param event: request body
    :type event: dict
    :return: response body
    :rtype: dict
    """
    validation_results = data_model.validate_request_body(event)
    response_body = {}

    if validation_results["is_valid"]:
        medications = medication.medication_required(event)

        response_body.update(
            {
                "evidence": {
                    "medications": medications["medications"],
                },
                "evidenceSummary": {
                    "relevantMedCount": medications["relevantMedCount"],
                    "totalMedCount": medications["totalMedCount"],
                },
                "claimSubmissionId": event['claimSubmissionId']
            }
        )
        logging.info(f"claimSubmissionId: {event['claimSubmissionId']}, message processed successfully")
    else:
        logging.info(f"claimSubmissionId: {event['claimSubmissionId']}, message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"
        response_body["claimSubmissionId"] = event['claimSubmissionId']

    return response_body


def assess_sufficiency_asthma(event: Dict):
    """
    Take a request that includes asthma related data, and return a suffficiency decision

    :param event: request body
    :type event: dict
    :return: response body
    :rtype: dict
    """
    validation_results = data_model.validate_request_body(event)
    response_body = {}

    if validation_results["is_valid"]:
        medications = medication.filter_categorize_mas_medication(event)
        conditions = condition.conditions_calculation(event)

        response_body.update(
            {
                "evidence": {
                    "medications": medications["allMedications"],
                    "conditions": conditions["conditions"],
                    "procedures": event["evidence"]["procedures"],
                    "documentsWithoutAnnotationsChecked": utils.docs_without_annotations_ids(event)
                },
                "evidenceSummary": {
                    "totalMedCount": medications["allMedicationsCount"],
                    "schedularMedicationOneYearCount": medications["schedularMedicationOneYearCount"],
                    "proceduresCount": len(event["evidence"]["procedures"]),
                    "totalConditionsCount": conditions["totalConditionsCount"],
                    "relevantConditionsLighthouseCount": conditions["relevantConditionsLighthouseCount"]
                },
                "claimSubmissionDateTime": event["claimSubmissionDateTime"],
                "disabilityActionType": event["disabilityActionType"],
                "claimSubmissionId": event['claimSubmissionId']
            }
        )
        logging.info(f"claimSubmissionId: {event['claimSubmissionId']}, message processed successfully")
    else:
        logging.info(f"claimSubmissionId: {event['claimSubmissionId']}, message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"
        response_body["claimSubmissionId"] = event['claimSubmissionId']

    return response_body
