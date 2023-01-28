package gov.va.vro.mockbipce.model;

import gov.va.vro.model.bipevidence.BipFileUploadPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.GeneratedValue;
import java.io.Serializable;
import java.util.UUID;

@RedisHash("PdfEvidenceFile")
@Getter
@Setter
@NoArgsConstructor
public class EvidenceFile implements Serializable {
  @Id
  @GeneratedValue(generator = "uuid")
  private UUID id;
  private String fileNumber;
  private BipFileUploadPayload payload;
  private byte[] content;
}
