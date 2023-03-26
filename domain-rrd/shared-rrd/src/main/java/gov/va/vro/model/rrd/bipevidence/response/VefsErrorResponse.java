package gov.va.vro.model.rrd.bipevidence.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** BIP Claims Evidence API error response. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VefsErrorResponse {

  private String uuid;

  private String code;

  private String message;
}
