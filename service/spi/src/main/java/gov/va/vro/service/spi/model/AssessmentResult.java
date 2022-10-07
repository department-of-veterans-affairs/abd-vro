package gov.va.vro.service.spi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResult {

  @Schema(description = "Veteran ICN")
  @JsonInclude
  private String veteranIcn;

  @Schema(description = "Contention diagnostic code")
  @JsonInclude
  private String diagnosticCode;

  @Schema(description = "Medical evidence supporting assessment")
  @JsonInclude
  private AbdEvidence evidence;

  @Schema(description = "Error message in the case of an error")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String errorMessage;

  @Schema(description = "Summary of evidence counts")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private EvidenceSummary evidenceSummary;
}
