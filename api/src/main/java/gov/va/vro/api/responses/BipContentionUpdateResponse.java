package gov.va.vro.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/** @author warren @Date 11/16/22 */
@Builder
@Getter
@Schema(
    name = "BipContentionUpdateResponse",
    description = "Indicate that the claim contention has been updated")
public class BipContentionUpdateResponse {
  private boolean updated;
  private long contentionId;
  private String message;
}
