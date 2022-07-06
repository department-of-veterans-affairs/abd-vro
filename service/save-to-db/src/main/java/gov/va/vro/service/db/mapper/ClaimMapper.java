package gov.va.vro.service.db.mapper;

import gov.va.starter.example.service.spi.db.model.Claim;
import gov.va.starter.example.service.spi.db.model.Veteran;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClaimMapper {

  VeteranEntity toVeteranEntity(Veteran veteran);

  ClaimEntity toClaimEntity(Claim request);

  Claim toClaim(ClaimEntity claimEntity);
}
