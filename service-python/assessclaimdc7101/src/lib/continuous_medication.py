
def continuous_medication_required(request_body):
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
    if medication["status"].lower() == "active":
      relevant_medications.append(medication)
  
  return relevant_medications
