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
  @Schema(description = "Diagnostic code", example = "6602")
  private final String diagnosticCode;

  @Schema(
      description = "JSON string providing data for pdf",
      example = "{\"first\":\"Cat\",\"last\": ...")
  private final String veteranInfo;

  @Schema(
      description = "JSON string providing data for pdf",
      example = "{\"bp_readings\":[ ... ], \"medications\":[ ... ]")
  private final String evidence;

  @JsonCreator
  public GeneratePdfRequest(
      @NonNull @JsonProperty("diagnosticCode") String diagnosticCode,
      @JsonProperty("veteran_info") String veteranInfo,
      @JsonProperty("evidence") String evidence) {
    this.diagnosticCode = diagnosticCode;
    this.veteranInfo = veteranInfo;
    this.evidence = evidence;
  }
}
