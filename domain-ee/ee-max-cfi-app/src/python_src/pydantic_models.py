from pydantic import BaseModel, conlist


class MaxRatingsForClaimForIncreaseRequest(BaseModel):
    diagnostic_codes: conlist(int, unique_items=True, min_items=1, max_items=100)


class Rating(BaseModel):
    diagnostic_code: int
    max_rating: float


class MaxRatingsForClaimForIncreaseResponse(BaseModel):
    ratings: list[Rating] = []
