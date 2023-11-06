from datetime import datetime

from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_camel


class Message(BaseModel):
    model_config = ConfigDict(populate_by_name=True, alias_generator=to_camel)

    key: str
    severity: str
    status: int | None = None
    text: str | None = None
    timestamp: datetime | None = None
    http_status: str | None = None


class GeneralResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True, alias_generator=to_camel)

    status_code: int
    status_message: str
    messages: list[Message] | None = None
