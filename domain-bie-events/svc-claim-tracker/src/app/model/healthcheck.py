from enum import Enum

from pydantic import BaseModel


class Status(str, Enum):
    HEALTHY = 'healthy'
    UNHEALTHY = 'unhealthy'


class HealthResponse(BaseModel):
    status: Status = Status.HEALTHY


class HealthErrorResponse(HealthResponse):
    status: Status = Status.UNHEALTHY
    errors: list[str] | None = None
