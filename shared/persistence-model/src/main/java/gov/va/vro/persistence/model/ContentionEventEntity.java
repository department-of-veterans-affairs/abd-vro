package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
  private Long claimId;

  @Column(name = "contention_id")
  private Long contentionId;

  @Column(name = "contention_type_code")
  private String contentionTypeCode;

  @Column(name = "diagnostic_type_code")
  private String diagnosticTypeCode;

  @Column(name = "event_details", columnDefinition = "jsonb")
  @Type(type = "jsonb")
  private Map<String, Object> eventDetails;
}
