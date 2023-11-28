package gov.va.vro.bip.model.cancel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.vro.bip.model.BipPayloadRequest;
import lombok.Builder;
import lombok.Getter;

/**
 * Cancel claim model specification.
 *
 * @author nelsestu @Date 10/24/23
 */
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelClaimRequest implements BipPayloadRequest {

  @JsonProperty("claimId")
  public long claimId;

  // for the full list of cancellation reasons see DSVA slack channel: benefits-vro :
  // https://dsva.slack.com/archives/C04PKJ7FQCE/p1697829905804139?thread_ts=1697823656.710359&cid=C04PKJ7FQCE
  @JsonProperty("lifecycleStatusReasonCode")
  private String lifecycleStatusReasonCode;

  @JsonProperty("closeReasonText")
  private String closeReasonText;
}
