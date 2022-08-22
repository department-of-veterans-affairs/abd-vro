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
        "date_of_claim": {"type": "string"},
        "diagnosticCode": {"type": "string"},
        "evidence":{
            "type": "dict",
            "schema": {
            "medications": {
                "required": True,
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
                        "dosageInstructions": {
                            "type": "list",
                            "default": [],
                            "schema": {"type": "string",
                            "default": ""}
                        },
                        "route": {"type": "string"},
                        "refills": {},
                        "duration": {"type": "string"},
                        "description": {
                            "type": "string",
                            "required": True
                            },
                        "notes": {
                            "type": "list",
                            "schema": {"type": "string"}
                        }
                    }
                }
            },
            "bp_readings": {
                "required":True,
                "type": "list",
                "schema": {
                    "type": "dict",
                    "schema": {
                        "diastolic": {
                            "type": "dict",
                            "required": True,
                            "schema": {
                                "value": {
                                    "type": "number",
                                    "required": True
                                    },
                                "code": {"type": "string"},
                                "display": {"type": "string"},
                                "unit": {"type": "string"}
                            }
                        },
                        "systolic": {
                            "type": "dict",
                            "required": True,
                            "schema": {
                                "value": {
                                    "type": "number",
                                    "required": True
                                    },
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
