package gov.va.vro.api.requests;

import gov.va.vro.api.model.AbdEvidence;
import gov.va.vro.api.model.VeteranInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(name = "GeneratePdfRequest", description = "Details for pdf generation")
public class GeneratePdfRequest {
  @NonNull
  @Schema(description = "Claim submission ID", example = "1234")
  private String claimSubmissionId;

  @NonNull
  @Schema(description = "Diagnostic code", example = "6602")
  private String diagnosticCode;

  @NonNull
  @Schema(description = "Veteran data for the pdf")
  private VeteranInfo veteranInfo;

  @NonNull
  @Schema(description = "Medical evidence supporting assessment")
  private AbdEvidence evidence;
}
