package gov.va.vro.bip.model.evidence.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** Simplified version of BIP Claims Evidence API happy return payload. */
@Getter
@Setter
@Builder
public class UploadResponse {
  private String uuid;
  private String currentVersionUuid;
}
