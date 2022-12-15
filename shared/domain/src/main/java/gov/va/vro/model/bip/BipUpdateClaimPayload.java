package gov.va.vro.model.bip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import gov.va.vro.model.event.Auditable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

/** @author warren @Date 11/7/22 */
@Builder
@Getter
@Schema(name = "BIPClaimUpdateRequest", description = "Set a claim status to RFD request")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BipUpdateClaimPayload implements Auditable {

  @NotBlank(message = "ID of the claim to be set to RFD.")
  @Schema(description = "claim ID", example = "1234")
  private String claimId;

  @Override
  public String getEventId() {
    return claimId;
  }
}
