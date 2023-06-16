package gov.va.vro.bip.model.evidence.response;

import lombok.*;

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
