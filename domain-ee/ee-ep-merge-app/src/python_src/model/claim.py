from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_camel


class ClaimDetail(BaseModel):
    """ Contention Summary with extra fields ignored. """
    model_config = ConfigDict(populate_by_name=True, alias_generator=to_camel)

    claim_id: int
    end_product_code: str | None = None
