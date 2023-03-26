package gov.va.vro.model.rrd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbdEvidenceWithSummary {

  @Schema(description = "Medical Evidence")
  private AbdEvidence evidence;

  @Schema(description = "Evidence Counts")
  private Map<String, Object> evidenceSummary;

  private String errorMessage;

  @Schema(description = "Claim submission id", example = "1234")
  private String claimSubmissionId;

  @Schema(description = "Namespace for the claimSubmissionId", example = "mas-Form526Submission")
  private String idType;

  @Schema(description = "Sufficient for Fast Tracking flag")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Boolean sufficientForFastTracking;
}
