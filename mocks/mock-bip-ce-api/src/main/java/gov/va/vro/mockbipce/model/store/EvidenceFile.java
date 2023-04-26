package gov.va.vro.mockbipce.model.store;

import gov.va.vro.model.rrd.bipevidence.BipFileUploadPayload;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class EvidenceFile {
  private String fileNumber;

  private UUID uuid;

  private BipFileUploadPayload payload;
  private byte[] content;
}
