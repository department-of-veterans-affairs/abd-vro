package gov.va.vro.api.requests;

import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.VeteranInfo;
import gov.va.vro.model.mas.ClaimCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(name = "GeneratePdfRequest", description = "Details for pdf generation")
public class GeneratePdfRequest {
  @NotBlank
  @Schema(description = "Claim submission ID", example = "1234")
  private String claimSubmissionId;

  @NotBlank
  @Schema(description = "Diagnostic code", example = "7101")
  private String diagnosticCode;

  @NotNull
  @Schema(description = "Veteran data for the pdf")
  private VeteranInfo veteranInfo;

  @Nullable
  @Schema(description = "Veteran file identifier", example = "12345")
  private String veteranFileId;

  private ClaimCondition conditions;

  @NonNull
  @Schema(description = "Medical evidence supporting assessment")
  private AbdEvidence evidence;

  @Nullable
  @Schema(
      description = "PDF template to generate",
      example = "v1",
      allowableValues = {"v1", "v2", "", null})
  private String pdfTemplate;

  @Schema(description = "Documents unavailable for automated review")
  private List<String> documentsWithoutAnnotationsChecked;
}
