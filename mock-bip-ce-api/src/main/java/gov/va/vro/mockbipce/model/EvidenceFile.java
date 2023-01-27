package gov.va.vro.mockbipce.model;

import gov.va.vro.model.bipevidence.BipFileUploadPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("PdfEvidenceFile")
@Getter
@Setter
@AllArgsConstructor
public class EvidenceFile implements Serializable {
  private String id;
  private BipFileUploadPayload payload;
  private byte[] content;
}
