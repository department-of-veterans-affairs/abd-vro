package gov.va.vro.api.rrd.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Bip file upload response.
 *
 * @author warren @Date 12/7/22
 */
@Builder
@Getter
@Setter
@Schema(
    name = "BipFileUploadResponse",
    description = "Indicate that the claim evidence file upload result.")
public class BipFileUploadResponse {
  private boolean uploaded;
  private String message;
}
