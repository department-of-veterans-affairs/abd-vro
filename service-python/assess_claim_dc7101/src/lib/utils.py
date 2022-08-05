from cerberus import Validator


def recursive_items(dictionary):
    for key, value in dictionary.items():
        if type(value) is dict:
            yield (key, value)
            yield from recursive_items(value)
        if type(value) is list and len(value) > 0 and type(value[0]) is dict:
            yield from recursive_items(value[0])
        else:
            yield (key, value)



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
                        "description": {
                            "type": "string",
                            "required":True
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

    error_dict = {}
    for key, value in recursive_items(v.errors):
        error_dict.update({key: value[0]})

    return {
        "is_valid": v.validate(request_body),
        "errors": error_dict
    }
