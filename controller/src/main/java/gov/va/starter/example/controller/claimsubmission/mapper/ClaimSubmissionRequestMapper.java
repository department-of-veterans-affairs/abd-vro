package gov.va.starter.example.controller.claimsubmission.mapper;

import gov.va.starter.example.api.claimsubmission.requests.ClaimSubmissionRequest;
import gov.va.starter.example.api.claimsubmission.requests.SubClaimSubmissionRequest;
import gov.va.starter.example.api.claimsubmission.responses.ClaimSubmissionResponse;
import gov.va.starter.example.api.claimsubmission.responses.SubClaimSubmissionResponse;
import gov.va.starter.example.api.responses.PagedResponse;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.starter.example.service.spi.claimsubmission.model.SubClaimSubmission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ClaimSubmissionRequestMapper {

  @Mapping(constant = "UNKNOWN_ID", target = "id")
  ClaimSubmission toModel(ClaimSubmissionRequest request);

  @Mapping(constant = "UNKNOWN_ID", target = "id")
  SubClaimSubmission toModel(SubClaimSubmissionRequest request);

  @Mapping(
      target = "fullName",
      expression = "java(String.format(\"%s %s\",src.getFirstName(),src.getLastName()))")
  ClaimSubmissionResponse toClaimSubmissionResponse(ClaimSubmission src);

  default ClaimSubmissionResponse toClaimSubmissionResponse(Optional<ClaimSubmission> src) {
    return toClaimSubmissionResponse(src.orElse(null));
  }

  /**
   * convert to PagedResponse<>.
   *
   * @param src Page<> object
   * @return PagedResponse<>
   */
  default PagedResponse<ClaimSubmissionResponse> toClaimSubmissionResponsePage(
      Page<ClaimSubmission> src) {
    return new PagedResponse<>(
        toClaimSubmissionResponseList(src.getContent()),
        src.getTotalPages(),
        src.getTotalElements(),
        src.getNumber(),
        src.getNumberOfElements());
  }

  List<ClaimSubmissionResponse> toClaimSubmissionResponseList(List<ClaimSubmission> src);

  SubClaimSubmissionResponse toSubClaimSubmissionResponse(SubClaimSubmission src);

  default SubClaimSubmissionResponse toSubClaimSubmissionResponse(
      Optional<SubClaimSubmission> src) {
    return toSubClaimSubmissionResponse(src.orElse(null));
  }

  List<SubClaimSubmissionResponse> toSubClaimSubmissionResponseList(List<SubClaimSubmission> src);

  /**
   * convert to PagedResponse<>.
   *
   * @param src Page<> object
   * @return PagedResponse<>
   */
  default PagedResponse<SubClaimSubmissionResponse> toSubClaimSubmissionResponsePage(
      Page<SubClaimSubmission> src) {
    return new PagedResponse<>(
        toSubClaimSubmissionResponseList(src.getContent()),
        src.getTotalPages(),
        src.getTotalElements(),
        src.getNumber(),
        src.getNumberOfElements());
  }
}
