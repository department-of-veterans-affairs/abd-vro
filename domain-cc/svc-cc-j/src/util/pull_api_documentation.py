"""
Make sure you are in svc-cc-j/src/ and have set your PYTHONPATH accordingly in order to execute
python3.10 pull_api_documentation.py
"""

import json
from fastapi import FastAPI
from fastapi.openapi.utils import get_openapi


def export_openapi(app: FastAPI, filename: str) -> None:
    """ Export the OpenAPI specification of a FastAPI app to a JSON file. """
    openapi_schema = get_openapi(
        title=app.title,
        version=app.version,
        routes=app.routes
    )

    with open(filename, 'w') as outfile:
        json.dump(openapi_schema, outfile, indent=4)

if __name__ == '__main__':
    from api import app
    export_openapi(app, 'fastapi.json')
    print('Done!')
