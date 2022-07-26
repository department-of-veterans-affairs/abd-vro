package gov.va.vro.controller.demo.mapper;

import gov.va.vro.api.demo.model.AbdClaim;
import gov.va.vro.service.spi.demo.model.ClaimPayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostClaimRequestMapper {

  ClaimPayload toModel(AbdClaim request);
}
