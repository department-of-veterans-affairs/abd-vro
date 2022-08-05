package gov.va.starter.example.api.claimsubmission.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Deprecated
@AllArgsConstructor
// @RequiredArgsConstructor
@Getter
@Schema(
    name = "SubClaimSubmissionResponse",
    description = "Metadata describing an Account resource and unique identifier")
public class SubClaimSubmissionResponse {

  @NonNull
  @Schema(description = "unique id of the SubClaimSubmission resource")
  private final String id;

  @NonNull
  @Schema(description = "username of the SubClaimSubmission holder")
  private final String userName;

  @NonNull
  @Schema(description = "Given name of the SubClaimSubmission holder")
  private final String firstName;

  @NonNull
  @Schema(description = "Family name of the SubClaimSubmission holder")
  private final String lastName;
}
