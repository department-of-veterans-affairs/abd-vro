package gov.va.vro.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Bip file upload response.
 *
 * @author warren @Date 12/7/22
 */
@Builder
@Getter
@Schema(
    name = "BipFileUploadResponse",
    description = "Indicate that the claim evidence file upload result.")
public class BipFileUploadResponse {
  private boolean uploaded;
  private String message;
}
