package gov.va.vro.service.db.mapper;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.service.spi.model.Claim;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClaimMapper {

  ClaimEntity toClaimEntity(Claim claim);

  @Mapping(target = "recordId", source = "id")
  Claim toClaim(ClaimEntity claimEntity);
}
