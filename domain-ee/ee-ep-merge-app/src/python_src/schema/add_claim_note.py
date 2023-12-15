from pydantic import ConfigDict
from pydantic.alias_generators import to_camel

from .request import GeneralRequest
from .response import GeneralResponse


class Request(GeneralRequest):
    model_config = ConfigDict(populate_by_name=True, alias_generator=to_camel)

    vbms_claim_id: int
    claim_notes: list[str]


class Response(GeneralResponse):
    pass
