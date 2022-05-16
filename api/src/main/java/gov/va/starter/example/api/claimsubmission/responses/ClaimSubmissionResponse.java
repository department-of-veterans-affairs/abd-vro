package gov.va.starter.example.api.claimsubmission.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
// @RequiredArgsConstructor
@Getter
@Schema(
    name = "ClaimSubmissionResponse",
    description = "Metadata describing an ClaimSubmission resource and unique identifier")
public class ClaimSubmissionResponse {

  @NonNull
  @Schema(
      description = "unique id of the ClaimSubmission resource",
      example = "dd373780-79fb-4285-8c9b-bf48a8014a68")
  private final String id;

  @NonNull
  @Schema(description = "username of the ClaimSubmission holder", example = "lvanpelt")
  private final String userName;

  @NonNull
  @Schema(
      description = "Representative PII of the ClaimSubmission holder",
      example = "123-456-7890")
  private final String pii;

  @NonNull
  @Schema(description = "Given name of the ClaimSubmission holder", example = "Lucy")
  private final String firstName;

  @NonNull
  @Schema(description = "Family name of the ClaimSubmission holder", example = "van Pelt")
  private final String lastName;

  @NonNull
  @Schema(
      description = "Constructed full name (given + family) of the ClaimSubmission holder",
      example = "Lucy van Pelt")
  private final String fullName;
}
