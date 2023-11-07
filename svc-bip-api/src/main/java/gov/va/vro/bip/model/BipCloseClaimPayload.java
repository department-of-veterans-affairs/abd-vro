package gov.va.vro.bip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Close claim model specification.
 *
 * @author nelsestu @Date 10/24/23
 */
@Builder
@Getter
@Schema(name = "BipCloseClaimPayload", description = "Cancel Claim request payload")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BipCloseClaimPayload {

  @JsonProperty("claimId")
  public long claimId;

  @JsonProperty("reason")
  public BipCloseClaimReason reason;

}
