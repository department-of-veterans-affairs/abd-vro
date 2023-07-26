from pydantic import BaseModel


class MaxRatingsForClaimForIncreaseRequest(BaseModel):
    diagnostic_codes: list[int]


class Rating(BaseModel):
    diagnostic_code: int
    max_rating: float


class MaxRatingsForClaimForIncreaseResponse(BaseModel):
    ratings: list[Rating] = []
