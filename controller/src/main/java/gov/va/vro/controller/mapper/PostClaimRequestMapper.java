package gov.va.vro.controller.mapper;

import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.service.spi.model.Claim;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostClaimRequestMapper {

  // Requests from v2/health-data-assessment use claimSubmissionId,
  // which needs to map to collectionId (to be used as the reference_id field in ClaimSubmission)
  @Mapping(target = "collectionId", source = "claimSubmissionId")
  Claim toModel(HealthDataAssessmentRequest request);
}
