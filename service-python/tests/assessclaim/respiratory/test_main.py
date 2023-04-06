import pytest

from assessclaimrespiratory.src.lib import main


@pytest.mark.parametrize(
    "request_body, response",
    [
        (
            {
                "evidence": {
                    "procedures": [
                        {
                            "description": "Heart surgery procedure",
                            "status": "completed",
                            "code": "33999",
                            "performedDate": "1990-04-06",
                        }
                    ],
                    "conditions": [{
                        "text": "Emphysema",
                        "code": "J43",
                        "recordedDate": "1990-04-06",
                        "status": "active"
                    },
                        {
                            "text": "Acute Respiratory Failure",
                            "code": "J96.0",
                            "recordedDate": "1990-04-06",
                            "status": "active"}]
                },
                "dateOfClaim": "2021-11-09",
                "claimSubmissionId": "1234"
            },
            {
                'claimSubmissionId': '1234',
                "evidence": {
                    "procedures": [
                        {
                            "description": "Heart surgery procedure",
                            "status": "completed",
                            "code": "33999",
                            "performedDate": "1990-04-06",
                        }
                    ],
                    "conditions": [{
                        "text": "Emphysema",
                        "code": "J43",
                        "recordedDate": "1990-04-06",
                        "status": "active"
                    },
                        {
                            "text": "Acute Respiratory Failure",
                            "code": "J96.0",
                            "recordedDate": "1990-04-06",
                            "status": "active"}]
                },
                "evidenceSummary": {"relevantProceduresCount": 1,
                                    "totalProceduresCount": 1,
                                    "respProcedures": ["Cardiac Catheterization"],
                                    "secondaryConditions": ['Acute Respiratory Failure'],
                                    "relevantConditionsCount": 2,
                                    "totalConditionsCount": 2},
                'sufficientForFastTracking': True
            },
        ),
        (
            {
                "evidence": {
                    "procedures": [
                    ],
                    "conditions": [{
                        "text": "Emphysema",
                        "code": "J43",
                        "recordedDate": "1990-04-06",
                        "status": "active"
                    }]
                },
                "dateOfClaim": "2021-11-09",
                "claimSubmissionId": "1234"
            },
            {
                'claimSubmissionId': '1234',
                "evidence": {
                    "procedures": [

                    ],
                    "conditions": [{
                        "text": "Emphysema",
                        "code": "J43",
                        "recordedDate": "1990-04-06",
                        "status": "active"
                    }]
                },
                "evidenceSummary": {"relevantProceduresCount": 0,
                                    "totalProceduresCount": 0,
                                    "respProcedures": [],
                                    "secondaryConditions": [],
                                    "relevantConditionsCount": 1,
                                    "totalConditionsCount": 1},
                'sufficientForFastTracking': False
            }
        )
    ]
)
def test_main(request_body, response):
    """
    Test the function that takes the request and returns the response

    :param request_body: request body with blood pressure readings and other data
    :type request_body: dict
    :param response: response after running data through algorithms
    :type response: dict
    """
    api_response = main.assess_respiratory_condition(request_body)

    assert api_response == response
