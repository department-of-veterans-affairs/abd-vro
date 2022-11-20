import pytest

from assessclaimdc6602.src.lib import utils


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
                    ]
                },
                "dateOfClaim": "2021-11-09",
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
                    ]
                },
                "dateOfClaim": 20211109,
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
                "dateOfClaim": ["must be of string type"],
            },
        ),
    ],
)
def test_validate_request_body(request_body, result_is_valid, errors):
    """
    Test function that determines if the blood pressure readings contain a readings that are within 1 month and 6 months of the date of claim

    :param dateOfClaim: string representation of the date of claim
    :type dateOfClaim: string
    :param bp_readings: list of blood pressure readings
    :type bp_readings: list
    :param result: boolean describing whether or not the blood pressure readings meet the specifications
    :type result: bool
    """
    result = utils.validate_request_body(request_body)
    assert result["is_valid"] == result_is_valid
    assert result["errors"] == errors
