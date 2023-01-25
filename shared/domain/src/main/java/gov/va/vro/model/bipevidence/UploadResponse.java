package gov.va.vro.model.bipevidence;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Simplified version of BIP Claims Evidence API happy return payload.
 **/
@Getter
@Setter
@Builder
public class UploadResponse {
  private String uuid;
  private String currentVersionUuid;
}