package gov.va.vro.controller.mapper;

import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.service.spi.model.Claim;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostClaimRequestMapper {

  Claim toModel(HealthDataAssessmentRequest request);
}
