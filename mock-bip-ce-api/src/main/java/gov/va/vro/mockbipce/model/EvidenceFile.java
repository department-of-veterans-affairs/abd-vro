package gov.va.vro.mockbipce.model;

import gov.va.vro.model.bipevidence.BipFileUploadPayload;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.UUID;

@RedisHash("PdfEvidenceFile")
@Getter
@Setter
@NoArgsConstructor
public class EvidenceFile implements Serializable {
  @Id private String id; // FileNumber

  private UUID uuid;

  private BipFileUploadPayload payload;
  private byte[] content;
}
