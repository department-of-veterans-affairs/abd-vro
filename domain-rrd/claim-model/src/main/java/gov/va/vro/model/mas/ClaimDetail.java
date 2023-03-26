package gov.va.vro.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaimDetail {

  @Schema(description = "Claim ID", example = "1020")
  private String benefitClaimId;

  @Schema(description = "Claim submission timestamp", example = "2023-02-08T18:45:59Z")
  @NotBlank
  private String claimSubmissionDateTime;

  @Schema(description = "Source of claim", example = "VA.GOV")
  @NotBlank
  private String claimSubmissionSource = "VA.GOV";

  @NotNull @Valid private ClaimCondition conditions;
}
