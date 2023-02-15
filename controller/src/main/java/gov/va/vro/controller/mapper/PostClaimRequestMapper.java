package gov.va.vro.controller.mapper;

import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.service.spi.model.Claim;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostClaimRequestMapper {


  // Requests from v1 include a claimSubmissionId. This is equivalent to the referenceId on the claimSubmission table, which is equivalent to a collection Id from MAS
  @Mapping(target = "collectionId", source = "claimSubmissionId")
  Claim toModel(HealthDataAssessmentRequest request);
}
