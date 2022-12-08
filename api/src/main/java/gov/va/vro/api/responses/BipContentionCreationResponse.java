package gov.va.vro.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/** @author warren @Date 11/16/22 */
@Builder
@Getter
@Schema(
    name = "BipContentionCreationResponse",
    description = "Indicate that the claim contention has been created or not")
public class BipContentionCreationResponse {
  private boolean created;
  private long contentionId;
  private String message;
}
