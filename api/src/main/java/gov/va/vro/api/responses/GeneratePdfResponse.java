package gov.va.vro.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Schema(
    name = "GeneratePdfResponse",
    description = "Metadata describing an GeneratePdfResponse resource")
public class GeneratePdfResponse {

  @NotNull
  @Schema(
      description = "JSON results",
      example = "{\"claimSubmissionId\": 0, \"status\": \"IN_PROGRESS\", \"pdf\": \"\"}")
  private final String pdfDocumentJson;
}
