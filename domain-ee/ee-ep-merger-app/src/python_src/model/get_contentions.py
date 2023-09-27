from pydantic import ConfigDict
from pydantic.alias_generators import to_camel
from src.python_src.model.contention import ContentionSummary
from src.python_src.model.request import GeneralRequest
from src.python_src.model.response import GeneralResponse


class Request(GeneralRequest):
    model_config = ConfigDict(populate_by_name=True, alias_generator=to_camel)

    claim_id: int


class Response(GeneralResponse):
    model_config = ConfigDict(populate_by_name=True, alias_generator=to_camel)

    contentions: list[ContentionSummary] | None = None
    pass
