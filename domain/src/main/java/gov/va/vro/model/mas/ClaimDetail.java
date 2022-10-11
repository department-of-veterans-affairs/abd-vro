package gov.va.vro.model.mas;

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

  private String benefitClaimId;

  private String claimSubmissionDateTime; // "3064-71-62T73:04:35",

  private String claimSubmissionSource = "VA.GOV";

  @NotNull private ClaimDetailConditions conditions;
}
