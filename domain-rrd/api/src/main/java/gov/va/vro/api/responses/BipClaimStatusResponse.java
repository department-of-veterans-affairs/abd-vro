package gov.va.vro.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Bip claim status response.
 *
 * @author warren @Date 11/7/22
 */
@Builder
@Getter
@Schema(
    name = "BIPClaimStatusResponse",
    description = "Indicate that the claim status has been updated")
public class BipClaimStatusResponse {
  private boolean updated;
  private String message;
}
