package gov.va.starter.example.api.claimsubmission.responses;

import gov.va.starter.example.api.responses.PagedResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

import java.util.List;

// this class is necessary for easier documentation in springdoc
// current versions cannot directly determine the type of the list in
// PagedResponse<ClaimSubmissionResponse>
@Deprecated
@Schema(
    name = "PagedAccountResponse",
    description = "Bundled list of resources with paging metadata")
public class PagedClaimSubmissionResponse extends PagedResponse<ClaimSubmissionResponse> {

  public PagedClaimSubmissionResponse(
      @NonNull List<ClaimSubmissionResponse> items,
      @NonNull Integer totalPages,
      @NonNull Long totalItems,
      @NonNull Integer pageNumber,
      @NonNull Integer pageSize) {
    super(items, totalPages, totalItems, pageNumber, pageSize);
  }
}
