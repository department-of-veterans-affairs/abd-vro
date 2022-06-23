package gov.va.vro.api.demo.requests;

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
    name = "GeneratePdfRequest",
    description = "Metadata describing an GeneratePdfRequest resource")
public class GeneratePdfRequest {

  @NonNull
  @Schema(description = "Type of contention", example = "hypertension")
  private final String contention;

  @Schema(
      description = "JSON string providing data for pdf",
      example = "{\"first\":\"Cat\",\"last\": ...")
  private final String patientInfo;

  @Schema(
      description = "JSON string providing data for pdf",
      example = "{\"bp_readings\":[ ... ], \"medications\":[ ... ]")
  private final String assessedData;

  @JsonCreator
  public GeneratePdfRequest(
      @NonNull @JsonProperty("contention") String contention,
      @JsonProperty("patient_info") String patientInfo,
      @JsonProperty("assessed_data") String assessedData) {
    this.contention = contention;
    this.patientInfo = patientInfo;
    this.assessedData = assessedData;
  }
}
