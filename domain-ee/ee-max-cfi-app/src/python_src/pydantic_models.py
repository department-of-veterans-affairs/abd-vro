from pydantic import BaseModel, Field, StrictInt
from typing_extensions import Annotated


class MaxRatingsForClaimForIncreaseRequest(BaseModel):
    diagnostic_codes: Annotated[list[StrictInt], Field(max_length=1000)]


class Rating(BaseModel):
    diagnostic_code: int
    max_rating: int


class MaxRatingsForClaimForIncreaseResponse(BaseModel):
    ratings: list[Rating] = []
