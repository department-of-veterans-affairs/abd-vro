package gov.va.vro.model;

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

  private boolean sufficientForFastTracking;
}
