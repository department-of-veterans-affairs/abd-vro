package gov.va.vro.controller.mapper;

import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.service.spi.model.Claim;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostClaimRequestMapper {

  Claim toModel(HealthDataAssessmentRequest request);
}
