from fastapi import FastAPI

app = FastAPI()

from pydantic_models import ClaimForIncrease, PredictedClassification
from typing import Optional


@app.post("/classifier")
def get_classification(
    claim_for_increase: ClaimForIncrease,
) -> Optional[PredictedClassification]:
    print(f'claim for increase: {claim_for_increase}')
    return {"classification_code": 6602, "classification_name": "asthma"}
