package gov.va.starter.example.api.claimsubmission.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Schema(
    name = "ClaimSubmissionRequest",
    description = "Metadata describing an ClaimSubmission resource")
public class ClaimSubmissionRequest {

  @NonNull
  @Schema(description = "username of the ClaimSubmission holder", example = "lvanpelt")
  private final String userName;

  @NonNull
  @Schema(description = "Representative PII of the ClaimSubmission holder", example = "123-45-6789")
  private final String pii;

  @NonNull
  @Schema(description = "Given name of the ClaimSubmission holder", example = "Lucille")
  private final String firstName;

  @NonNull
  @Schema(description = "Family name of the ClaimSubmission holder", example = "Van Pelt")
  private final String lastName;

  @NonNull
  @Schema(
      description = "Identifier for the original claim submission",
      example = "89d6a168-2780-456e-a836-fda6419fedbc")
  private final String submissionId;

  @NonNull
  @Schema(description = "Identifier for the claimant", example = "123456789")
  private final String claimantId;

  @NonNull
  @Schema(description = "Type of contention", example = "hypertension")
  private final String contentionType;

  /**
   * Create object from json.
   *
   * @param userName username of ClaimSubmission holder
   * @param pii private information of ClaimSubmission holder
   * @param firstName firstname of ClaimSubmission holder
   * @param lastName lastname of ClaimSubmission holder
   */
  @JsonCreator
  public ClaimSubmissionRequest(
      @NonNull @JsonProperty("userName") String userName,
      @NonNull @JsonProperty("pii") String pii,
      @NonNull @JsonProperty("firstName") String firstName,
      @NonNull @JsonProperty("lastName") String lastName,
      @NonNull @JsonProperty("submissionId") String submissionId,
      @NonNull @JsonProperty("claimantId") String claimantId,
      @NonNull @JsonProperty("contentionType") String contentionType) {

    this.userName = userName;
    this.pii = pii;
    this.firstName = firstName;
    this.lastName = lastName;
    this.submissionId = submissionId;
    this.claimantId = claimantId;
    this.contentionType = contentionType;
  }
}
