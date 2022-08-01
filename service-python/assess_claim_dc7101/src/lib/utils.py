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
        "diagnosticCode": {"type": "string"},
        "evidence": {
            "required": True,
            "type": "dict",
            "schema": {
                "medications": {
                    "type": "list",
                    "schema": {
                        "type": "dict",
                        "schema": {
                            "authoredOn": {
                                "type": "string"
                            },
                            "status": {
                                "type": "string",
                            },
                            "dosageInstruction": {
                                "type": "list",
                                "schema": {"type": "string"}
                            },
                            "route": {"type": "string"},
                            "refills": {},
                            "duration": {"type": "string"},
                            "description": {"type": "string"},
                            "notes": {
                                "type": "list",
                                "schema": {"type": "string"}
                            }
                        }
                    }
                },
                "bp_readings": {
                    "type": "list",
                    "schema": {
                        "type": "dict",
                        "schema": {
                            "diastolic": {
                                "type": "dict",
                                "schema": {
                                    "value": {"type": "number"},
                                    "code": {"type": "string"},
                                    "display": {"type": "string"},
                                    "unit": {"type": "string"}
                                }
                            },
                            "systolic": {
                                "type": "dict",
                                "schema": {
                                    "value": {"type": "number"},
                                    "code": {"type": "string"},
                                    "display": {"type": "string"},
                                    "unit": {"type": "string"}
                                }
                            },
                            "date": {"type": "string"},
                            "practitioner": {"type": "string"},
                            "organization": {"type": "string"}
                        }
                    }
                }
            }
        }
    }
    v = Validator(schema)

    return {
        "is_valid": v.validate(request_body),
        "errors": v.errors
    }
