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
    name = "AssessHealthDataRequest",
    description = "Metadata describing an AssessHealthDataRequest resource")
public class AssessHealthDataRequest {

  @NonNull
  @Schema(description = "Type of contention", example = "hypertension")
  private final String contention;

  @NonNull
  @Schema(description = "JSON string response from Lighthouse Observations API",
      example = "{\"entry\":[{\"search\":{\"mode\":\"match\"},\"resource\": ...")
  private final String bp_observations;

  @JsonCreator
  public AssessHealthDataRequest(
      @NonNull @JsonProperty("contention") String contention,
      @NonNull @JsonProperty("bp_observations") String bp_observations) {
    this.contention = contention;
    this.bp_observations = bp_observations;
  }
}
