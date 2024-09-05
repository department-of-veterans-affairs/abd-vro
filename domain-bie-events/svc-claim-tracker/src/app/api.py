from contextlib import asynccontextmanager
from typing import Any, AsyncIterator

import uvicorn
from fastapi import Depends, FastAPI, Request, status
from fastapi.encoders import jsonable_encoder
from fastapi.responses import JSONResponse
from sqlalchemy.exc import SQLAlchemyError
from sqlmodel import Session

from app.database.config import get_session, init_db
from app.database.tracked_claim_repo import tracked_claim_repo
from app.model.healthcheck import HealthErrorResponse, HealthResponse
from app.model.tracked_claim import TrackedClaim
from app.schema.track_claim import TrackClaimRequest, TrackClaimResponse
from app.util.logger import logger

CONNECT_TO_DATABASE_FAILURE = 'Cannot connect to database.'


@asynccontextmanager
async def lifespan(api: FastAPI) -> AsyncIterator[None]:
    init_db()
    yield


app = FastAPI(
    title='Claim Tracker for BIE Claim Events',
    description='This API accepts requests to track a claim lifecycle by BIE Claim Events.',
    contact={},
    version='v0.1',
    license={'name': 'CCO 1.0', 'url': 'https://github.com/department-of-veterans-affairs/abd-vro/blob/master/LICENSE.md'},
    lifespan=lifespan,
)


@app.get(
    '/',
    summary='Welcome to the Claim Tracker API',
    status_code=status.HTTP_200_OK,
)
async def welcome() -> dict[str, Any]:
    """Welcome user to the API and direct them to OpenAPI spec."""
    return {
        'status': 'ok',
        'message': 'Welcome to the Claim Tracker API',
        'links': {
            'docs': '/docs',
            'health check': '/health',
            'track claim': '/track/v1/claim',
        },
    }


@app.get(
    '/health',
    summary='Check health of Claim Tracker',
    response_model=HealthResponse,
    status_code=status.HTTP_200_OK,
    responses={
        status.HTTP_200_OK: {'model': HealthResponse, 'description': 'Claim Tracker is healthy'},
        status.HTTP_500_INTERNAL_SERVER_ERROR: {'model': HealthErrorResponse, 'description': 'Claim Tracker is unhealthy'},
    },
    response_model_exclude_none=True,
)
def get_health_status(db: Session = Depends(get_session)) -> dict[str, str] | JSONResponse:
    errors = health_check_errors(db)
    if errors:
        return JSONResponse(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, content={'status': 'unhealthy', 'errors': errors})
    return {'status': 'healthy'}


def health_check_errors(db: Session) -> list[str]:
    errors = []
    if not tracked_claim_repo.is_ready(db):
        errors.append(CONNECT_TO_DATABASE_FAILURE)
    return errors


@app.post(path='/track/v1/claim', summary='Track a Claim', status_code=status.HTTP_202_ACCEPTED, response_model=TrackClaimResponse)
def track_claim(request: Request, track_request: TrackClaimRequest, db: Session = Depends(get_session)) -> TrackedClaim | JSONResponse:
    errors = health_check_errors(db)
    if errors:
        logger.error(f'event=requestFailed errors={errors}')
        return JSONResponse(status_code=500, content=jsonable_encoder({'method': 'POST', 'url': str(request.url), 'errors': errors}))

    tracked_claim = TrackedClaim.model_validate(track_request)
    tracked_claim_repo.add(db, tracked_claim)
    return tracked_claim


@app.exception_handler(SQLAlchemyError)
async def sqlalchemy_exception_handler(request: Request, err: SQLAlchemyError) -> JSONResponse:
    msg = str(err).replace('\n', ' ')
    logger.error(f"event=requestFailed method={request.method} url={request.url} resource={'Database'} error={msg}")
    return JSONResponse(status_code=500, content=jsonable_encoder({'method': request.method, 'url': str(request.url), 'errors': [CONNECT_TO_DATABASE_FAILURE]}))


if __name__ == '__main__':
    uvicorn.run(app, host='localhost', port=8150)
