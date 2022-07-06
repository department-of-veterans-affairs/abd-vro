hypertension_medications = {
    "benazepril",
    "lotensin",
    "captopril",
    "capoten",
    "enalapril",
    "enalaprilat",
    "fosinopril",
    "monopril",
    "lisinopril",
    "prinivil",
    "zestril",
}

def continuous_medication_required(request_body):
  """
  Determine if there is the veteran requires continuous medication for hypertension

  :param request_body: request body
  :type request_body: dict
  :return: response body indicating success or failure with additional attributes 
  :rtype: dict
  """

  continuous_medication_required_calculation = {
        "success": True
  }
  veterans_medication = request_body["medication"]
  medication_list = [med["text"] for med in veterans_medication]
  vet_is_taking_htn_medication = False
  for medication in medication_list:
    if (medication.lower() in hypertension_medications):
      vet_is_taking_htn_medication = True
      break

  continuous_medication_required_calculation["continuous_medication_required"] = vet_is_taking_htn_medication

  return continuous_medication_required_calculation
