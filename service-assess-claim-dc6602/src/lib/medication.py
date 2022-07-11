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
  active_medication = []
  continuous_medication_required = {
        "success": True
  }
  veterans_medication = request_body["medication"]
  medication_list = [med["text"] for med in veterans_medication]
  vet_is_taking_asthma_medication = False
  for medication in medication_list:
    for keyword in [x.lower() for x in asthma_medications]:
        if keyword in medication.lower():
            active_medication.append(medication)
            vet_is_taking_asthma_medication = True

  continuous_medication_required["vet_is_taking_asthma_medication"] = vet_is_taking_asthma_medication

  return continuous_medication_required
