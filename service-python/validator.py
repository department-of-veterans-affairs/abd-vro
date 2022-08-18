from cerberus import Validator


def validate_request_body(request_body):
    """
    Validates that the request body conforms to the expected data format

    :param request_body: request body converted from json
    :type request_body: dict
    :return: dict with boolean result showing if request is valid and if not, any applicable errors
    :rtype: dict
    """
    request_body =  {x:y for x,y in request_body.items() if y is not None}
    schema = {
       "veteranIcn": {"type": "string"},
        "date_of_claim": {"type": "string"},
        "diagnosticCode": {"type": "string"},
        "evidence":{
            "type": "dict",
            "schema": {
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
                            'default': [],
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