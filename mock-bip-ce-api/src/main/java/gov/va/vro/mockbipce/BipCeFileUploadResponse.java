package gov.va.vro.mockbipce;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BipCeFileUploadResponse {
  private String owner;
  private String uuid;
  private String currentVersionUuid;
  private String md5;
}
