from pydantic import ConfigDict
from pydantic.alias_generators import to_camel

from .contention import ExistingContention
from .request import GeneralRequest
from .response import GeneralResponse


class Request(GeneralRequest):
    model_config = ConfigDict(populate_by_name=True, alias_generator=to_camel)

    claim_id: int
    update_contentions: list[ExistingContention]


class Response(GeneralResponse):
    pass
