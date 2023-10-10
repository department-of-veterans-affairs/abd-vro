from pydantic import ConfigDict
from pydantic.alias_generators import to_camel

from .request import GeneralRequest
from .response import GeneralResponse


class Request(GeneralRequest):
    model_config = ConfigDict(populate_by_name=True, alias_generator=to_camel)

    claim_id: int
    lifecycle_status_reason_code: str
    close_reason_text: str | None = None


class Response(GeneralResponse):
    pass
