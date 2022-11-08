package gov.va.vro.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/** @author warren @Date 11/7/22 */
@Builder
@Getter
@Schema(
    name = "BIPClaimUpdateResponse",
    description = "Indicate that the claim status has been updated")
public class BipClaimResponse {
  private boolean updated;
  private String message;
}
