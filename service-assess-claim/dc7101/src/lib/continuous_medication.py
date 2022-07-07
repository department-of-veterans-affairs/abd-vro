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
  medications_meta = []
  continuous_medication_required_calculation = {
        "success": True
  }
  veterans_medication = request_body["medication"]
  print(veterans_medication)
  print([x.lower() for x in hypertension_medications])
  medication_list = [med["text"] for med in veterans_medication]
  vet_is_taking_htn_medication = False
  for medication in medication_list:
    print(medication)
    for keyword in [x.lower() for x in hypertension_medications]:
      if (keyword in medication.lower()):
        vet_is_taking_htn_medication = True
        medications_meta.append(medication)

  continuous_medication_required_calculation["continuous_medication_required"] = vet_is_taking_htn_medication

  return continuous_medication_required_calculation
