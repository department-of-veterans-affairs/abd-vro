package gov.va.vro.api.demo.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
@Schema(
    name = "AssessHealthDataResponse",
    description = "Metadata describing an AssessHealthDataResponse resource")
public class AssessHealthDataResponse {

  @NonNull
  @Schema(description = "JSON results", example = "{\"bp_readings\": [ ... ]}")
  private final String bpReadingsJson;
}
