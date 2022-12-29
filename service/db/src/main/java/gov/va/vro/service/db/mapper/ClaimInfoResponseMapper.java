package gov.va.vro.service.db.mapper;

import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.persistence.model.ClaimEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ContentionInfoMapper.class)
public interface ClaimInfoResponseMapper {
  @Mapping(target = "veteranIcn", source = "veteran.icn")
  ClaimInfoResponse toClaimInfoResponse(ClaimEntity claimEntity);
}
