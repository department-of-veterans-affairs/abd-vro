package gov.va.vro.service.db.mapper;

import gov.va.starter.example.service.spi.db.model.Claim;
import gov.va.vro.persistence.model.ClaimEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClaimMapper {

  @Mapping(target = "claimId", source = "claimSubmissionId")
  ClaimEntity toClaimEntity(Claim request);

  Claim toClaim(ClaimEntity claimEntity);
}
