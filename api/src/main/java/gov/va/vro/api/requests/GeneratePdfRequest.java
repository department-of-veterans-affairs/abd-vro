package gov.va.vro.api.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(name = "GeneratePdfRequest", description = "Details for pdf generation")
public class GeneratePdfRequest {
  @NonNull
  @Schema(description = "Claim submission ID", example = "0")
  private String claimSubmissionId;

  @NonNull
  @Schema(description = "Diagnostic code", example = "6602")
  private String diagnosticCode;

  @NonNull
  @Schema(
      description = "JSON string providing data for pdf",
      example =
          "{'first': 'test','middle': 'test', 'last': 'test', 'suffix': 'test', 'birthdate': '2000-10-20")
  private String veteranInfo;

  @NonNull
  @Schema(description = "Medical evidence supporting assessment")
  private String evidence;
}
