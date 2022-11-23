from datetime import datetime

asthma_medications = {
    "Aerochamber",
    "Albuterol",
    "Beclomethasone",
    "Benralizumab",
    "Budesonide",
    "Ciclesonide",
    "Fluticasone",
    "Levalbuterol",
    "Mepolizumab",
    "Methylprednisolone",
    "Mometasone",
    "Montelukast",
    "Omalizumab",
    "Prednisone",
    "Reslizumab",
    "Salmeterol",
    "Theophylline",
    "Zafirlukast",
    "Zileuton",
    "Asthma",
    "Breath",
    "Inhal",
    "Puff",
    "SOB",
}


def medication_required(request_body):
    """
    Determine if there is the veteran requires continuous medication for hypertension
    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """
    response = {}
    relevant_medications = []
    other_medications = []

    veterans_medication = request_body["evidence"]["medications"]

    for medication in veterans_medication:
        if "status" not in medication.keys():
            other_medications.append(medication)
            medication["asthmaRelevant"] = "false"
            continue
        if medication["status"].lower() == "active":
            flagged = False
            medication_display = medication["description"]
            for keyword in [x.lower() for x in asthma_medications]:
                if keyword in medication_display.lower():
                    medication["asthmaRelevant"] = "true"
                    relevant_medications.append(medication)
                    flagged = True
                    break
            if not flagged:
                medication["asthmaRelevant"] = "false"
                other_medications.append(medication)

    relevant_medications = sorted(
        relevant_medications,
        key=lambda i: datetime.strptime(i["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date(),
        reverse=True,
    )

    other_medications = sorted(
        other_medications,
        key=lambda i: datetime.strptime(i["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date(),
        reverse=True,
    )

    response["relevantMedCount"] = len(relevant_medications)
    relevant_medications.extend(other_medications)
    response["totalMedCount"] = len(relevant_medications)
    response["medications"] = relevant_medications

    return response
