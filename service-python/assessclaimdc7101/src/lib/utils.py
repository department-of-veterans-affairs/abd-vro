from cerberus import Validator


def validate_request_body(request_body):
    """
    Validates that the request body conforms to the expected data format

    :param request_body: request body converted from json
    :type request_body: dict
    :return: dict with boolean result showing if request is valid and if not, any applicable errors
    :rtype: dict
    """
    schema = {
        "veteranIcn": {"type": "string"},
        "dateOfClaim": {"type": "string"},
        "diagnosticCode": {"type": "string"},
        "disabilityActionType": {"type": "string"},
        "disabilityClassificationCode": {"type": "string"},
        "ratedDisabilityId": {"type": "string"},
        "evidence": {
            "type": "dict",
            "schema": {
                "medications": {
                    "type": "list",
                    "schema": {
                        "type": "dict",
                        "schema": {
                            "authoredOn": {"type": "string"},
                            "status": {"type": "string"},
                            "dosageInstructions": {
                                "type": "list",
                                "nullable": True,
                                "schema": {"type": "string", "default": ""},
                            },
                            "route": {"type": "string", "nullable": True},
                            "refills": {},
                            "duration": {"type": "string", "nullable": True},
                            "description": {"type": "string"},
                            "notes": {
                                "type": "list",
                                "nullable": True,
                                "schema": {"type": "string"},
                            },
                        },
                    },
                },
                "bp_readings": {
                    "required": True,
                    "type": "list",
                    "schema": {
                        "type": "dict",
                        "schema": {
                            "diastolic": {
                                "type": "dict",
                                "required": True,
                                "schema": {
                                    "value": {"type": "number", "required": True},
                                    "code": {"type": "string"},
                                    "display": {"type": "string"},
                                    "unit": {"type": "string"},
                                },
                            },
                            "systolic": {
                                "type": "dict",
                                "required": True,
                                "schema": {
                                    "value": {"type": "number", "required": True},
                                    "code": {"type": "string"},
                                    "display": {"type": "string"},
                                    "unit": {"type": "string"},
                                },
                            },
                            "date": {"type": "string"},
                            "practitioner": {"type": "string", "nullable": True},
                            "organization": {"type": "string", "nullable": True},
                        },
                    },
                },
                "procedures": {
                    "type": "list",
                    "schema": {
                        "type": "dict",
                        "schema": {
                            "code": {
                                "type": "string",
                            },
                            "codeSystem": {
                                "type": "string"
                            },
                            "text": {
                                "type": "string"
                            },
                            "performedDate": {
                                "type": "string"
                            },
                            "status": {
                                "type": "string",
                            },
                        },
                    },
                },
                "conditions": {
                    "type": "list",
                    "schema": {
                        "type": "dict",
                        "schema": {
                            "code": {
                                "type": "string",
                                "required": True
                            },
                            "status": {
                                "type": "string",
                            },
                            "text": {
                                "type": "string"
                            },
                            "onsetDate": {
                                "type": "string",
                            },
                            "abatementDate": {
                                "type": "string",
                                "nullable": True
                            }
                        }
                    }
                }
            }
        }
    }
    v = Validator(schema)
    v.allow_unknown = True

    return {"is_valid": v.validate(request_body), "errors": v.errors}
