from datetime import datetime

from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_camel

from .request import GeneralRequest
from .response import GeneralResponse


class Request(GeneralRequest):
    pass


class SpecialIssueType(BaseModel):
    model_config = ConfigDict(populate_by_name=True, alias_generator=to_camel)

    name: str
    code: str
    description: str | None = None
    deactive_date: datetime | None = None


class Response(GeneralResponse):
    model_config = ConfigDict(populate_by_name=True, alias_generator=to_camel)

    code_name_pairs: list[SpecialIssueType] | None = None
