from fastapi import FastAPI

app = FastAPI()

from pydantic_models import ClaimForIncrease, PredictedClassification
from typing import Optional

@app.get("/")
async def root():
    return {"message": "Hello World2"}


@app.post("/get_classification")
def get_classification(claim_for_increase: ClaimForIncrease) -> Optional[PredictedClassification]:
    return {"classification_code": 6602, "classification_name": "asthma"}
