package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "evidence_summary_document")
public class EvidenceSummaryDocumentEntity extends BaseEntity {

  @ManyToOne private ContentionEntity contention;

  // number of evidence data points found to support fast tracking the claim
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Map<String, String> evidenceCount;

  private String documentName;

  private UUID folderId;

  private OffsetDateTime uploadedAt;
}
