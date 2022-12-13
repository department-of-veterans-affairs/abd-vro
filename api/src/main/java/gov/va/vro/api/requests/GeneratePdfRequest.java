package gov.va.vro.api.requests;

import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.ServiceLocation;
import gov.va.vro.model.VeteranInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

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

  @Schema(description = "Veteran service locations for the pdf")
  private List<ServiceLocation> serviceLocations;

  @NonNull
  @Schema(description = "Medical evidence supporting assessment")
  private AbdEvidence evidence;
}
