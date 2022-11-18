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
                                        "text": "Malignant neoplasm of vertebral column"},
                                       {"code": "C91.52",
                                        "text": "Adult T-cell lymphoma/leukemia (HTLV-1-associated), in relapse"}
                                       ]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "neck",
                {'conditions': [{"code": "C41.2",
                                 "suggestedCategory": "Bone",
                                 "text": "Malignant neoplasm of vertebral column"}],
                 'conditionsCount': 2,
                 'relevantConditionsCount': 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"},
                                       {"code": "C25",
                                        "text": "Malignant neoplasm of pancreas"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "pancreatic",
                {
                    "conditions": [{"code": "C25",
                                    "text": "Malignant neoplasm of pancreas"}],
                    "conditionsCount": 2,
                    "relevantConditionsCount": 1,
                },
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"},
                                       {"code": "C43.72",
                                        "text": "Malignant melanoma of left lower limb, including hip"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "melanoma",
                {'conditions': [
                    {"code": "C43.72",
                     "text": "Malignant melanoma of left lower limb, including hip"}],
                 'conditionsCount': 2,
                 'relevantConditionsCount': 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"},
                                       {"code": "C61",
                                        "text": "Malignant neoplasm of prostate"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "prostate",
                {'conditions': [{"code": "C61",
                                 "text": "Malignant neoplasm of prostate"}],
                 'conditionsCount': 2,
                 'relevantConditionsCount': 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"},
                                       {"code": "C56",
                                        "text": "Malignant neoplasm of ovary"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "gyn",
                {'conditions': [{"code": "C56",
                                 "text": "Malignant neoplasm of ovary",
                                 "suggestedCategory": "Ovarian"}],
                 'conditionsCount': 2,
                 'relevantConditionsCount': 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"},
                                       {"code": "C41.2",
                                        "text": "Malignant neoplasm of vertebral column"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "head",
                {'conditions': [{'code': 'C41.0',
                                 'suggestedCategory': "Bone",
                                 'text': 'Malignant neoplasm of bones of skull and face'}],
                 'conditionsCount': 2,
                 'relevantConditionsCount': 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"},
                                       {"code": "C63.7",
                                        "text": "Malignant neoplasm of other specified male genital organs"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "male_reproductive",
                {'conditions': [{"code": "C63.7",
                                 "suggestedCategory": "Multiple",
                                 "text": "Malignant neoplasm of other specified male genital organs"}],
                 'conditionsCount': 2,
                 'relevantConditionsCount': 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"},
                                       {"code": "C18.1",
                                        "text": "Malignant neoplasm of appendix"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "gi",
                {'conditions': [{"code": "C18.1",
                                 "text": "Malignant neoplasm of appendix"}],
                 'conditionsCount': 2,
                 'relevantConditionsCount': 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"},
                                       {"code": "C50",
                                        "text": "Malignant neoplasm of breast"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "breast",
                {'conditions': [{"code": "C50",
                                 "text": "Malignant neoplasm of breast"}],
                 'conditionsCount': 2,
                 'relevantConditionsCount': 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"},
                                       {"code": "C64",
                                        "text": "Malignant neoplasm of kidney, except renal pelvis"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "kidney",
                {'conditions': [{"code": "C64",
                                 "text": "Malignant neoplasm of kidney, except renal pelvis"}],
                 'conditionsCount': 2,
                 'relevantConditionsCount': 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"},
                                       {"code":"C71",
                                        "text": "Malignant neoplasm of brain"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "brain",
                {'conditions': [{"code":"C71",
                                 "text": "Malignant neoplasm of brain"}],
                 'conditionsCount': 2,
                 'relevantConditionsCount': 1},
        ),
        (
                {
                    "evidence": {
                        "medications": [],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"},
                                       {"code": "C34.0",
                                        "text": "Malignant neoplasm of main bronchus"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "respiratory",
                {'conditions': [{"code": "C34.0",
                                 "suggestedCategory": "Bronchus",
                                 "text": "Malignant neoplasm of main bronchus"}],
                 'conditionsCount': 2,
                 'relevantConditionsCount': 1},
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
