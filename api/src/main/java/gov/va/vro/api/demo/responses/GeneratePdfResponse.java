package gov.va.vro.api.demo.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
@Schema(
    name = "GeneratePdfResponse",
    description = "Metadata describing an GeneratePdfResponse resource")
public class GeneratePdfResponse {

  @NonNull
  @Schema(
      description = "JSON results",
      example = "{\"claimSubmissionId\": 0, \"status\": \"COMPLETE\", \"pdf\": \"...\"}")
  private final String pdfDocumentJson;
}
