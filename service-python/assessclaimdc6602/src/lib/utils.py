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
                            "type": "string",
                            "required": True
                        },
                        "status": {
                            "type": "string",
                            "required": True
                        },
                        "dosageInstructions": {
                            "type": "list",
                            "nullable": True,
                            "schema": {"type": "string",
                            "default": ""}
                        },
                        "route": {
                            "type": "string",
                            "nullable": True
                            },
                        "refills": {},
                        "duration": {
                            "type": "string",
                            "nullable": True
                            },
                        "description": {
                            "type": "string",
                            "required": True
                            },
                        "notes": {
                            "type": "list",
                            "nullable": True,
                            "schema": {"type": "string"}
                        }
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