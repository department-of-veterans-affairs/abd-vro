from typing import Optional

from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()
MODEL_VERSION = "0.0.1"
VALID_CLASSIFICATION_CODES = set([])
CLASSIFICATION_BY_CONTENTION_NAME = {}


@app.get("/")
async def root():
    return {"message": "Hello World2"}


class VeteranSubmittedContention(BaseModel):
    contention_name: str


class Classification(BaseModel):
    contention_name: Optional[str]
    contention_code: Optional[str]
    model_version: str


@app.post("/get_classification")
async def get_classification(item: VeteranSubmittedContention):
    contention_name = item.get("contention_name", "").lower()
    if contention_name in VALID_CLASSIFICATION_CODES:
        response = {
            "contention_id": CLASSIFICATION_BY_CONTENTION_NAME[contention_name],
            "contention_name": contention_name,
        }
    else:
        response = {
            "contention_id": None,
            "contention_name": None,
        }
    response["model_version"] = MODEL_VERSION
    return response
