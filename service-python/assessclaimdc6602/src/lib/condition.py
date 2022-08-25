asthma_conditions = {
    '233687002',
    '13151001', 
    '16584951000119101',
    '445427006',
    '10676111000119102',
    '10692761000119107',
    '92807009', 
    '418395004', 
    '10675431000119106',
    '703954005',
    '57607007',
    '125021000119107',
    '10675751000119107',
    '11641008',
    '41553006',
    '427603009', 
    '404806001',
    '2360001000004109',
    '195967001',
    '675391000119101',
    '34015007',
    '708093000',
    '195949008',
    '72301000119103', 
    '41997000',
    '405944004',
    '19849005',
    '10676391000119108',
    '56968009',
    '404808000',
    '782520007',
    '735588005',
    '10676231000119102',
    '736056000',
    '10676511000119109',
    '423889005',
    '10676671000119102',
    '442025000', 
    '10674991000119104',
    '281239006',
    '233683003',
    '99031000119107',
    '370220003',
    '93432008',
    '233690008',
    '10675551000119104',
    '63088003',
    '195977004',
    '233679003',
    '427679007',
    '233691007',
    '10742121000119100',
    '829976001',
    '735589002',
    '233678006',
    '389145006',
    '10675631000119109',
    '703953004',
    '1751000119100',
    '10675911000119109',
    '407674008',
    '10674711000119105',
    '425969006',
    '10675711000119106',
    '424199006',
    '55570000',
    '18041002',
    '401000119107',
    '10675871000119106',
    '409663006',
    '707444001',
    '404804003',
    '10692721000119102'
}

persistent_asthma = {
    "370218001", 
    "370221004", 
    "424643009", 
    "225057002",
    "370219009", 
    "266361008", 
    "786836003", 
    "733858005", 
    "762521001", 
    "426979002", 
    "233688007", 
    "31387002", 
    "426656000",
    "427295004",
    "707445000",
    "1741000119102",
    "708090002",
    "707511009",
    "734905008",
    "707513007",
    "708094006",
    "707447008",
    "707512002",
    "707446004",
    "12428000",
    "782513000",
    "708095007",
    "708096008",
    "734904007",
    "707981009",
    "707979007",
    "707980005",
    "125011000119100",
    "124991000119109",
    "125001000119103",
    "10676431000119100",
    "10676591000119100",
    "10675991000119100",
    "J82.83",
    }   
    
def conditions_calculation(request_body):
  """
  Determine if there is the veteran requires continuous medication for hypertension

  :param request_body: request body
  :type request_body: dict
  :return: response body indicating success or failure with additional attributes 
  :rtype: dict
  """
  calculation = {"success": True, "mild-persistent-asthma-or-greater": False}
  response = {}
  relevant_conditions = []

  veterans_conditions = request_body["evidence"]["conditions"]
  for condition in veterans_conditions:
    condition_code = condition["code"]
    if condition_code in asthma_conditions:
        relevant_conditions.append(condition)
    elif condition_code in persistent_asthma:
        relevant_conditions.append(condition)
        calculation["mild-persistent-asthma-or-greater"]= True

  response["conditions"] = relevant_conditions
  response["persistent_calculation"] = calculation

  return response
