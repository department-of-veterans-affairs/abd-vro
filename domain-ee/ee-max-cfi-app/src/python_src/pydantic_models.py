from pydantic import BaseModel, StrictInt, Field
from typing_extensions import Annotated


class MaxRatingsForClaimForIncreaseRequest(BaseModel):
    diagnostic_codes: Annotated[list[StrictInt], Field(max_items=1000)]


class Rating(BaseModel):
    diagnostic_code: int
    max_rating: int


class MaxRatingsForClaimForIncreaseResponse(BaseModel):
    ratings: list[Rating] = []
