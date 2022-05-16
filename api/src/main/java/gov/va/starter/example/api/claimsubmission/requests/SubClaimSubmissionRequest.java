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
    name = "SubClaimSubmissionRequest",
    description = "Metadata describing an SubClaimSubmission resource")
public class SubClaimSubmissionRequest {

  @NonNull
  @Schema(description = "username of the SubClaimSubmission holder", example = "lvanpelt")
  private final String userName;

  @NonNull
  @Schema(description = "Given name of the SubClaimSubmission holder", example = "Lucille")
  private final String firstName;

  @NonNull
  @Schema(description = "Family name of the SubClaimSubmission holder", example = "Van Pelt")
  private final String lastName;

  /**
   * Create object from json.
   *
   * @param userName username of ClaimSubmission holder
   * @param firstName firstname of ClaimSubmission holder
   * @param lastName lastname of ClaimSubmission holder
   */
  @JsonCreator
  public SubClaimSubmissionRequest(
      @NonNull @JsonProperty("userName") String userName,
      @NonNull @JsonProperty("firstName") String firstName,
      @NonNull @JsonProperty("lastName") String lastName) {

    this.userName = userName;
    this.firstName = firstName;
    this.lastName = lastName;
  }
}
