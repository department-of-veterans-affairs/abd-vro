package gov.va.vro.bip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

/**
 * BIP update claim payload.
 *
 * @author warren @Date 11/7/22
 */
@Builder
@Getter
@Schema(name = "BIPClaimUpdateRequest", description = "Set a claim status to RFD request")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BipUpdateClaimPayload {

  @NotBlank(message = "ID of the claim to be set to RFD.")
  @Schema(description = "claim ID", example = "1234")
  private String claimId;
}
