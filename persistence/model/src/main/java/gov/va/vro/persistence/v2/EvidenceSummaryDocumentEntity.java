package gov.va.vro.persistence.v2;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "evidence_summary_document")
public class EvidenceSummaryDocumentEntity extends BaseEntity {

  @ManyToOne private ContentionEntity contention;

  // number of evidence data points found to support fast tracking the claim
  @Type(type = "json")
  @Column(columnDefinition = "jsonb")
  private Map<String, String> evidenceCount;

  private String documentName;
}
