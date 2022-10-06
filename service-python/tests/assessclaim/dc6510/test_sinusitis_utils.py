import pytest

from assessclaimdc6510.src.lib import utils


@pytest.mark.parametrize(
    "request_body, result_is_valid, errors",
    [
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": "Capoten",
                            "status": "active",
                            "authoredOn": "1952-04-06T04:00:00Z",
                        }
                    ],
                    "conditions": [{"text": "Chronic maxillary sinusitis",
                                    "code": "35923002",
                                    "status": "active"},
                                   {"text": "Chronic maxillary sinusitis",
                                    "code": "J32.0",
                                    "status": "active"}],
                    "procedures": [{"text": "Nasal/sinus endoscopy, surgical, with dilation (eg, balloon "
                                            "dilation); sphenoid "
                                            "sinus ostium",
                                    "code": "31297",
                                    "status": "completed"}]
                },
                "date_of_claim": "2021-11-09",
            },
            True,
            {},
        ),
        (
            {
                "evidence": {
                    "medications": [
                        {
                            "description": 123,
                            "status": "active",
                            "authoredOn": "1952-04-06T04:00:00Z",
                        }
                    ],
                    "conditions": [],
                    "procedures": []
                },
                "date_of_claim": 20211109,
            },
            False,
            {
                "evidence": [
                    {
                        "medications": [
                            {0: [{"description": ["must be of string type"]}]}
                        ]
                    }
                ],
                "date_of_claim": ["must be of string type"],
            },
        ),
    ],
)
def test_validate_request_body(request_body, result_is_valid, errors):
    """
    Test function that determines if the blood pressure readings contain a readings that are within 1 month and 6 months of the date of claim

    :param date_of_claim: string representation of the date of claim
    :type date_of_claim: string
    :param bp_readings: list of blood pressure readings
    :type bp_readings: list
    :param result: boolean describing whether or not the blood pressure readings meet the specifications
    :type result: bool
    """
    result = utils.validate_request_body(request_body)
    assert result["is_valid"] == result_is_valid
    assert result["errors"] == errors
