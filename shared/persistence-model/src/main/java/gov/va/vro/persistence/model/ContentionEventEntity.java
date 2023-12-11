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

  @Column(name = "date_added")
  private LocalDateTime dateAdded;

  @Column(name = "date_completed")
  private LocalDateTime dateCompleted;

  @Column(name = "date_updated")
  private LocalDateTime dateUpdated;

  @Column(name = "actor_station")
  private String actorStation;

  @Column(name = "automation_indicator")
  private boolean automationIndicator;

  @Column(name = "benefit_claim_type_code")
  private String benefitClaimTypeCode;

  @Column(name = "contention_status_type_code")
  private String contentionStatusTypeCode;

  @Column(name = "current_lifecycle_status")
  private String currentLifecycleStatus;

  @Column(name = "details")
  private String details;

  @Column(name = "event_time")
  private LocalDateTime eventTime;

  @Column(name = "journal_status_type_code")
  private String journalStatusTypeCode;

  @Column(name = "veteran_participant_id")
  private long veteranParticipantId;

  @Column(name = "event_details")
  private String eventDetails;
}
