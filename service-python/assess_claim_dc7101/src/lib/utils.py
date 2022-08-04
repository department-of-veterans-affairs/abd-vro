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
        "vasrd": {"type": "string"},
        "evidence":{
            "type": "dict",
            "schema": {
            "condition": {
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
                            "required": True
                        },
                        "text": {
                            "type": "string"
                        },
                        "onset_date": {
                            "type": "string",
                            "required": True
                        },
                        "abatement_date": {
                            "type": "string"
                        }
                    }
                }
            },
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
                "required":True,
                "type": "list",
                "schema": {
                    "type": "dict",
                    "require_all": True,
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
            },
            "procedure": {
                "type": "list",
                "schema": {
                    "type": "dict",
                    "schema": {
                        "code": {
                            "type": "string",
                            "required": True
                        },
                        "code_system": {
                            "type": "string"
                        },
                        "text": {
                            "type": "string"
                        },
                        "performed_date": {
                            "type": "string",
                            "required": True
                        },
                        "status": {
                            "type": "string",
                            "required": True
                        },
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
