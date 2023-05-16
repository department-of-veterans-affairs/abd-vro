package gov.va.vro.bip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.NotBlank;

/**
 * BIP request claim contention payload.
 *
 * @author warren @Date 11/16/22
 */
@Builder
@Getter
@Schema(name = "BIPClaimUpdateRequest", description = "Set a claim status to RFD request")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BipRequestClaimContentionPayload {
  @JsonProperty("claimId")
  @NotBlank(message = "ID of the claim to be retrieved.")
  @Schema(description = "claim ID", example = "12345")
  private long claimId;
}
