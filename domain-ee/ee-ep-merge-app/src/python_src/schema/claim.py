from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_camel


class BenefitClaimType(BaseModel):
    """Benefit Claim Type with extra fields ignored."""

    model_config = ConfigDict(populate_by_name=True, alias_generator=to_camel)

    name: str | None = None
    code: str | None = None


class ClaimDetail(BaseModel):
    """Contention Summary with extra fields ignored."""

    model_config = ConfigDict(populate_by_name=True, alias_generator=to_camel)

    claim_id: int
    benefit_claim_type: BenefitClaimType | None = None
    end_product_code: str | None = None
    temp_station_of_jurisdiction: str | None = None
    claim_lifecycle_status: str | None = None
