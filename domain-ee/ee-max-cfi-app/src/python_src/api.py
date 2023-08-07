import logging
import sys

from fastapi import FastAPI, HTTPException
from pydantic_models import (MaxRatingsForClaimForIncreaseRequest,
                             MaxRatingsForClaimForIncreaseResponse)
from util.lookup_table import MAX_RATINGS_BY_CODE, get_max_rating

app = FastAPI(
    title="Max Ratings for CFI",
    description="Maps a list of disabilities to their max rating.",
    contact={},
    version="v0.1",
    license={
        "name": "CCO 1.0",
        "url": "https://github.com/department-of-veterans-affairs/abd-vro/blob/master/LICENSE.md"
    },
    servers=[
        {
            "url": "/cfi",
            "description": "Max Ratings for CFI",
        },
    ]
)

logging.basicConfig(
    format="[%(asctime)s] %(levelname)-8s %(message)s",
    level=logging.INFO,
    datefmt="%Y-%m-%d %H:%M:%S",
    stream=sys.stdout,
)


@app.get("/health")
def get_health_status():
    if not len(MAX_RATINGS_BY_CODE):
        raise HTTPException(status_code=500, detail="Max Rating by Diagnostic Code Lookup table is empty.")

    return {"status": "ok"}


@app.post("/max-ratings")
def get_max_ratings(
        claim_for_increase: MaxRatingsForClaimForIncreaseRequest, ) -> MaxRatingsForClaimForIncreaseResponse:
    ratings = []
    for dc_ in set(claim_for_increase.diagnostic_codes):
        dc = validate_diagnostic_code(dc_)
        max_rating = get_max_rating(dc)
        if max_rating:
            rating = {
                "diagnostic_code": dc,
                "max_rating": max_rating,
            }
            ratings.append(rating)

    response = {
        "ratings": ratings
    }

    logging.info(f"event=getMaxRating response={response}")
    return response


# Rough boundaries of diagnostic codes as shown by document at
# (https://www.ecfr.gov/current/title-38/part-4/appendix-Appendix B to Part 4)
# TODO should be replaced with map of valid diagnostic codes and checked to see if the dc is in map.
def validate_diagnostic_code(dc: int) -> int:
    if dc < 5000 or dc > 10000:
        raise HTTPException(status_code=400, detail=f"The diagnostic code received is invalid: dc={dc}")
    return dc
