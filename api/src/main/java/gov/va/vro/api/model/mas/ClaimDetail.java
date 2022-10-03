package gov.va.vro.api.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaimDetail {

  private String benefitclaimid;
  private String claimsubmissiondatetime; // "3064-71-62T73:04:35",
  private String claimsubmissionsource = "VA.GOV";
  @NotNull private ClaimDetailConditions conditions;
}
