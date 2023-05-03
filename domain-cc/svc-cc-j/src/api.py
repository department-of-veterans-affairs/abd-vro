from fastapi import FastAPI

app = FastAPI()

from pydantic_models import ClaimForIncrease, PredictedClassification
from typing import Optional


@app.post("/get_classification")
def get_classification(
        claim_for_increase: ClaimForIncrease,
) -> Optional[PredictedClassification]:
    print(f'received diagnostic code: {claim_for_increase["diagnostic_code"]}')
    return {"classification_code": 6602, "classification_name": "asthma"}
