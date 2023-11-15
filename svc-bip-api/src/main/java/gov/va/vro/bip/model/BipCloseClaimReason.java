package gov.va.vro.bip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request to cancel a claim.
 *
 * @author warren @Date 11/14/22
 */
@Getter
@Setter
@Builder
@Schema(name = "BipCloseClaimReason", description = "Claim cancellation details")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BipCloseClaimReason {

  // for the full list of cancellation reasons see DSVA slack channel: benefits-vro :
  // https://dsva.slack.com/archives/C04PKJ7FQCE/p1697829905804139?thread_ts=1697823656.710359&cid=C04PKJ7FQCE
  @Schema(
      description = "life cycle status reason codes from bip-api/claims/lc_status_reason_types",
      example = "60")
  @NotNull
  private String lifecycleStatusReasonCode;

  @Schema(
      description = "explanation for claim cancellation",
      example = "Duplicate claim consolidated")
  private String closeReasonText;
}
