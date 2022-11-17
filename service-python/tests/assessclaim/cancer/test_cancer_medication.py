import pytest

from assessclaimcancer.src.lib import medication


@pytest.mark.parametrize(
    "request_body, cancer_type, response",
    [
        (
                {
                    "evidence": {
                        "medications": [
                            {
                                "description": "0.4 ML Otrexup 56.3 MG/ML Auto-Injector",
                                "status": "active",
                                "authoredOn": "1952-04-06T04:00:00Z",
                            },
                            {
                                "authoredOn": "1962-04-06T04:00:00Z",
                                "description": "CISplatin 50 MG",
                                "status": "active",
                            },
                        ],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "head",
                {"medications": [
                    {
                        "authoredOn": "1962-04-06T04:00:00Z",
                        "description": "CISplatin 50 MG",
                        "status": "active",
                        "conditionRelated": True
                    },
                    {
                        "description": "0.4 ML Otrexup 56.3 MG/ML Auto-Injector",
                        "status": "active",
                        "authoredOn": "1952-04-06T04:00:00Z",
                        "conditionRelated": True
                    }],
                    "relevantMedCount": 2,
                    "totalMedCount": 2}
        ),
        (
                {
                    "evidence": {
                        "medications": [
                            {
                                "description": "0.4 ML Otrexup 56.3 MG/ML Auto-Injector",
                                "status": "active",
                                "authoredOn": "1952-04-06T04:00:00Z",
                            },
                            {
                                "authoredOn": "1962-04-06T04:00:00Z",
                                "description": "CISplatin 50 MG",
                                "status": "active",
                            },
                        ],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "neck",
                {"medications": [
                    {
                        "authoredOn": "1962-04-06T04:00:00Z",
                        "description": "CISplatin 50 MG",
                        "status": "active",
                        'suggestedCategory': 'Multiple',
                        "conditionRelated": True
                    },
                    {
                        "description": "0.4 ML Otrexup 56.3 MG/ML Auto-Injector",
                        "status": "active",
                        "authoredOn": "1952-04-06T04:00:00Z",
                        'suggestedCategory': 'Multiple',
                        "conditionRelated": True
                    }],
                    "relevantMedCount": 2,
                    "totalMedCount": 2}
        ),
        (
                {
                    "evidence": {
                        "medications": [
                            {
                                "description": "0.4 ML Otrexup 56.3 MG/ML Auto-Injector",
                                "status": "active",
                                "authoredOn": "1952-04-06T04:00:00Z",
                            },
                            {
                                "authoredOn": "1962-04-06T04:00:00Z",
                                "description": "CISplatin 50 MG",
                                "status": "active",
                            },
                        ],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "male_reproductive",
                {"medications": [
                    {
                        "authoredOn": "1962-04-06T04:00:00Z",
                        "description": "CISplatin 50 MG",
                        "status": "active",
                        'suggestedCategory': 'Multiple',
                        "conditionRelated": True
                    }
                ],
                    "relevantMedCount": 1,
                    "totalMedCount": 2}
        ),
        (
                {
                    "evidence": {
                        "medications": [
                            {
                                "description": "0.4 ML Otrexup 56.3 MG/ML Auto-Injector",
                                "status": "active",
                                "authoredOn": "1952-04-06T04:00:00Z",
                            },
                            {
                                "authoredOn": "1962-04-06T04:00:00Z",
                                "description": "paclitaxel 6 MG/ML",
                                "status": "active",
                            },
                        ],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "gyn",
                {"medications": [
                    {
                        "authoredOn": "1962-04-06T04:00:00Z",
                        "description": "paclitaxel 6 MG/ML",
                        "status": "active",
                        'suggestedCategory': 'Cervical, Uterine',
                        "conditionRelated": True
                    }
                ],
                    "relevantMedCount": 1,
                    "totalMedCount": 2}
        ),
        (
                {
                    "evidence": {
                        "medications": [
                            {
                                "description": "0.4 ML Otrexup 56.3 MG/ML Auto-Injector",
                                "status": "active",
                                "authoredOn": "1952-04-06T04:00:00Z",
                            },
                            {
                                "authoredOn": "1962-04-06T04:00:00Z",
                                "description": "CISplatin 50 MG",
                                "status": "active",
                            },
                        ],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "prostate",
                {"medications": [
                    {
                        "authoredOn": "1962-04-06T04:00:00Z",
                        "description": "CISplatin 50 MG",
                        "status": "active",
                        "conditionRelated": True
                    }
                ],
                    "relevantMedCount": 1,
                    "totalMedCount": 2}
        ),
        (
                {
                    "evidence": {
                        "medications": [
                            {
                                "description": "everolimus 0.75 MG",
                                "status": "active",
                                "authoredOn": "1952-04-06T04:00:00Z",
                            },
                            {
                                "authoredOn": "1962-04-06T04:00:00Z",
                                "description": "CISplatin 50 MG",
                                "status": "active",
                            },
                        ],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "pancreatic",
                {"medications": [
                    {
                        "description": "everolimus 0.75 MG",
                        "status": "active",
                        "authoredOn": "1952-04-06T04:00:00Z",
                        "conditionRelated": True
                    }],
                    "relevantMedCount": 1,
                    "totalMedCount": 2}
        ),
        (
                {
                    "evidence": {
                        "medications": [
                            {
                                "description": "aldesleukin 22000000 UNT [Proleukin]",
                                "status": "active",
                                "authoredOn": "1952-04-06T04:00:00Z",
                            },
                            {
                                "authoredOn": "1962-04-06T04:00:00Z",
                                "description": "CISplatin 50 MG",
                                "status": "active",
                            },
                        ],
                        "conditions": [{"code": "C41.0",
                                        "text": "Malignant neoplasm of bones of skull and face"}]
                    },
                    "dateOfClaim": "2021-11-09",
                },
                "melanoma",
                {"medications": [
                    {
                        "description": "aldesleukin 22000000 UNT [Proleukin]",
                        "status": "active",
                        "authoredOn": "1952-04-06T04:00:00Z",
                        'suggestedCategory': 'Systemic chemotherapy',
                        "conditionRelated": True
                    }],
                    "relevantMedCount": 1,
                    "totalMedCount": 2}
        )
    ],
)
def test_medication(request_body, cancer_type, response):
    """
    Test the function that takes the request and returns the response

    :param request_body: request body with blood pressure readings and other data
    :type request_body: dict
    :param response: response after running data through algorithms
    :type response: dict
    """
    api_response = medication.medication_match(request_body, cancer_type)

    assert api_response == response
