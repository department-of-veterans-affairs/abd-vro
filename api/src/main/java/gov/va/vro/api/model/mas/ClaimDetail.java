package gov.va.vro.api.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaimDetail {

  @JsonProperty("benefitclaimid")
  private String benefitClaimId;

  @JsonProperty("claimsubmissiondatetime")
  private String claimSubmissionDateTime; // "3064-71-62T73:04:35",

  @JsonProperty("claimsubmissionsource")
  private String claimSubmissionSource = "VA.GOV";

  @NotNull private ClaimDetailConditions conditions;
}
