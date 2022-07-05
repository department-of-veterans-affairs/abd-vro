package gov.va.vro.service.db.mapper;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import gov.va.vro.service.db.model.ClaimRequest;
import gov.va.vro.service.db.model.Veteran;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClaimRequestMapper {

  VeteranEntity toVeteranEntity(Veteran veteran);

  ClaimEntity toClaimEntity(ClaimRequest request);
}
