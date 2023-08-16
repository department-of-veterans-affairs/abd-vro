from pydantic import BaseModel, conint, conlist


class MaxRatingsForClaimForIncreaseRequest(BaseModel):
    diagnostic_codes: conlist(conint(strict=True), unique_items=True, min_items=1, max_items=100)


class Rating(BaseModel):
    diagnostic_code: int
    max_rating: int


class MaxRatingsForClaimForIncreaseResponse(BaseModel):
    ratings: list[Rating] = []
