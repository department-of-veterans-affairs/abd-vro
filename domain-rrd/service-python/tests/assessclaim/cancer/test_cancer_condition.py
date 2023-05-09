import pytest
from assessclaimcancer.src.lib import condition


@pytest.mark.parametrize(
    "request_body, cancer_type, response",
    [
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.2",
                                        "text": "Malignant neoplasm of vertebral column",
                                        "onsetDate": "2014-11-27"},
                                       {"code": "C91.52",
                                        "text": "Adult T-cell lymphoma/leukemia (HTLV-1-associated), in relapse",
                                        "onsetDate": "2016-11-27"}
                                       ]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "neck",
                {"conditionsMeetDateRequirements": False,
                 "conditions": [{"code": "C41.2",
                                 "suggestedCategory": "Bone",
                                 "text": "Malignant neoplasm of vertebral column",
                                 "onsetDate": "2014-11-27"}],
                 "conditionsCount": 2,
                 "relevantConditionsCount": 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face",
                                        "onsetDate": "2014-11-27"},
                                       {"code": "C25",
                                        "text": "Malignant neoplasm of pancreas",
                                        "onsetDate": "2021-11-1"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "pancreatic",
                {
                    "conditionsMeetDateRequirements": True,
                    "conditions": [{"code": "C25",
                                    "text": "Malignant neoplasm of pancreas",
                                    "onsetDate": "2021-11-1"}],
                    "conditionsCount": 2,
                    "relevantConditionsCount": 1,
                },
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face",
                                        "onsetDate": "2014-11-27"},
                                       {"code": "C43.72",
                                        "text": "Malignant melanoma of left lower limb, including hip",
                                        "onsetDate": "2014-11-27"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "melanoma",
                {"conditionsMeetDateRequirements": False,
                 "conditions": [
                     {"code": "C43.72",
                      "text": "Malignant melanoma of left lower limb, including hip",
                      "onsetDate": "2014-11-27"}],
                 "conditionsCount": 2,
                 "relevantConditionsCount": 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face",
                                        "onsetDate": "2014-11-27"},
                                       {"code": "C61",
                                        "text": "Malignant neoplasm of prostate",
                                        "onsetDate": "2021-11-1"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "prostate",
                {"conditionsMeetDateRequirements": True,
                 "conditions": [{"code": "C61",
                                 "text": "Malignant neoplasm of prostate",
                                 "onsetDate": "2021-11-1"}],
                 "conditionsCount": 2,
                 "relevantConditionsCount": 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face",
                                        "onsetDate": "2014-11-27"},
                                       {"code": "C56",
                                        "text": "Malignant neoplasm of ovary",
                                        "onsetDate": "2014-11-27"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "gyn",
                {"conditionsMeetDateRequirements": False,
                 "conditions": [{"code": "C56",
                                 "text": "Malignant neoplasm of ovary",
                                 "suggestedCategory": "Ovarian",
                                 "onsetDate": "2014-11-27"}],
                 "conditionsCount": 2,
                 "relevantConditionsCount": 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face",
                                        "onsetDate": "2020-11-27"},
                                       {"code": "C41.2",
                                        "text": "Malignant neoplasm of vertebral column",
                                        "onsetDate": "2014-11-27"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "head",
                {"conditionsMeetDateRequirements": True,
                 "conditions": [{"code": "C41.0",
                                 "suggestedCategory": "Bone",
                                 "text": "Malignant neoplasm of bones of skull and face",
                                 "onsetDate": "2020-11-27"}],
                 "conditionsCount": 2,
                 "relevantConditionsCount": 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face",
                                        "onsetDate": "2014-11-27"},
                                       {"code": "C63.7",
                                        "text": "Malignant neoplasm of other specified male genital organs",
                                        "onsetDate": "2014-11-27"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "male_reproductive",
                {"conditionsMeetDateRequirements": False,
                 "conditions": [{"code": "C63.7",
                                 "suggestedCategory": "Multiple",
                                 "text": "Malignant neoplasm of other specified male genital organs",
                                 "onsetDate": "2014-11-27"}],
                 "conditionsCount": 2,
                 "relevantConditionsCount": 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face",
                                        "onsetDate": "2014-11-27"},
                                       {"code": "C18.1",
                                        "text": "Malignant neoplasm of appendix",
                                        "onsetDate": "2014-11-27"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "gi",
                {"conditionsMeetDateRequirements": False,
                 "conditions": [{"code": "C18.1",
                                 "text": "Malignant neoplasm of appendix",
                                 "onsetDate": "2014-11-27"}],
                 "conditionsCount": 2,
                 "relevantConditionsCount": 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face",
                                        "onsetDate": "2014-11-27"},
                                       {"code": "C50",
                                        "text": "Malignant neoplasm of breast",
                                        "onsetDate": "2014-11-27"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "breast",
                {"conditionsMeetDateRequirements": False,
                 "conditions": [{"code": "C50",
                                 "text": "Malignant neoplasm of breast",
                                 "onsetDate": "2014-11-27"}],
                 "conditionsCount": 2,
                 "relevantConditionsCount": 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face",
                                        "onsetDate": "2014-11-27"},
                                       {"code": "C64",
                                        "text": "Malignant neoplasm of kidney, except renal pelvis",
                                        "onsetDate": "2014-11-27"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "kidney",
                {"conditionsMeetDateRequirements": False,
                 "conditions": [{"code": "C64",
                                 "text": "Malignant neoplasm of kidney, except renal pelvis",
                                 "onsetDate": "2014-11-27"}],
                 "conditionsCount": 2,
                 "relevantConditionsCount": 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face",
                                        "onsetDate": "2014-11-27"},
                                       {"code": "C71",
                                        "text": "Malignant neoplasm of brain",
                                        "onsetDate": "2014-11-27"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "brain",
                {"conditionsMeetDateRequirements": False,
                 "conditions": [{"code": "C71",
                                 "text": "Malignant neoplasm of brain",
                                 "onsetDate": "2014-11-27"}],
                 "conditionsCount": 2,
                 "relevantConditionsCount": 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face",
                                        "onsetDate": "2014-11-27"},
                                       {"code": "C34.0",
                                        "text": "Malignant neoplasm of main bronchus",
                                        "onsetDate": "2014-11-27"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "respiratory",
                {"conditionsMeetDateRequirements": False,
                 "conditions": [{"code": "C34.0",
                                 "suggestedCategory": "Bronchus",
                                 "text": "Malignant neoplasm of main bronchus",
                                 "onsetDate": "2014-11-27"}],
                 "conditionsCount": 2,
                 "relevantConditionsCount": 1},
        ),

    ],
)
def test_condition(request_body, cancer_type, response):
    """
    Test the function that takes the request and returns the response

    :param request_body: request body with blood pressure readings and other data
    :type request_body: dict
    :param response: response after running data through algorithms
    :type response: dict
    """
    api_response = condition.active_cancer_condition(request_body, cancer_type)

    assert api_response == response
