package gov.va.vro.bip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

/**
 * BIP request claim payload.
 *
 * @author warren @Date 11/16/22
 */
@Builder
@Getter
@Schema(name = "BipClaimInfoRequest", description = "Get a claim details request")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BipRequestClaimPayload {
  @JsonProperty("claimId")
  @NotBlank(message = "ID of the claim to be retrieved.")
  @Schema(description = "claim ID", example = "1234")
  private long claimId;
}
