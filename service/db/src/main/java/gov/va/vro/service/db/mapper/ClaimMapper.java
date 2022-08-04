package gov.va.vro.service.db.mapper;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.service.spi.model.Claim;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClaimMapper {

  ClaimEntity toClaimEntity(Claim claim);

}
