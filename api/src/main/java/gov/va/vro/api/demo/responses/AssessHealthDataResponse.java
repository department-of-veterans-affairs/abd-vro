package gov.va.vro.api.demo.responses;

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
public class AssessHealthDataResponse {

  @NonNull
  @Schema(description = "JSON results", example = "")
  private final String bpReadingsJson;
}
