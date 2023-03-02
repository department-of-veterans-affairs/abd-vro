package gov.va.vro.persistence.model;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import java.util.Map;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "evidence_summary_document")
@TypeDef(name = "json", typeClass = JsonType.class)
public class EvidenceSummaryDocumentEntity extends BaseEntity {

  @ManyToOne private ContentionEntity contention;

  // number of evidence data points found to support fast tracking the claim
  @Type(type = "json")
  @Column(columnDefinition = "jsonb")
  private Map<String, String> evidenceCount;

  private String documentName;

  private UUID folderId;
}
