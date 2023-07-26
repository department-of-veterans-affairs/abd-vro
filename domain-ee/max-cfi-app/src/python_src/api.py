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
    for dc in set(claim_for_increase.diagnostic_codes):
        max_rating = get_max_rating(dc)
        if max_rating:
            rating = {
                "diagnostic_code": dc,
                "max_rating": max_rating,
            }
            ratings.append(rating)
        else:
            raise HTTPException(status_code=404, detail=f"Could not find max rating for diagnostic_code={dc}")

    response = {
        "ratings": ratings
    }

    logging.info(f"event=getMaxRating response={response}")
    return response
