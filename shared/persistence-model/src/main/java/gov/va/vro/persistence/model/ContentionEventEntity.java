package gov.va.vro.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "bie_contention_event")
public class ContentionEventEntity extends BaseEntity {

  @Column(name = "notified_at")
  private LocalDateTime notifiedAt;

  @Column(name = "occurred_at")
  private LocalDateTime occurredAt;

  @Column(name = "event_type")
  private String eventType;

  @Column(name = "claim_id")
  private long claimId;

  @Column(name = "contention_id")
  private long contentionId;

  @Column(name = "contention_type_code")
  private String contentionTypeCode;

  @Column(name = "contention_classification_name")
  private String contentionClassificationName;

  @Column(name = "diagnostic_type_code")
  private String diagnosticTypeCode;
}
