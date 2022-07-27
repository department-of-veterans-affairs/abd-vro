package gov.va.vro.controller.demo.mapper;

import gov.va.vro.api.demo.requests.HealthDataAssessmentRequest;
import gov.va.vro.service.spi.demo.model.ClaimPayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostClaimRequestMapper {

  ClaimPayload toModel(HealthDataAssessmentRequest request);
}
