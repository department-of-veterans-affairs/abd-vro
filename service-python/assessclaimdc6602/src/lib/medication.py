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
  relevant_medications = []

  veterans_medication = request_body["evidence"]["medications"]
  for medication in veterans_medication:
    medication_display = medication["description"]
    for keyword in [x.lower() for x in asthma_medications]:
      if (keyword in medication_display.lower()):
        relevant_medications.append(medication)
  
  return relevant_medications
