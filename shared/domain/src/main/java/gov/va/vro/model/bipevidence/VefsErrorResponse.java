package gov.va.vro.model.bipevidence;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * BIP Claims Evidence API error response.
 **/
@Getter
@Setter
@Builder
public class VefsErrorResponse {

  private String uuid;

  private String code;

  private String message;
}
