from pydantic import BaseModel, conint, conlist


class MaxRatingsForClaimForIncreaseRequest(BaseModel):
    diagnostic_codes: conlist(conint(strict=True), max_items=1000)


class Rating(BaseModel):
    diagnostic_code: int
    max_rating: int


class MaxRatingsForClaimForIncreaseResponse(BaseModel):
    ratings: list[Rating] = []
