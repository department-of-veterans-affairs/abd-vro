package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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

  @Column(name = "event")
  private String eventType;

  @Column(name = "event_details")
  private String eventDetails;

  // TODO: Add event details to be extracted from eventDetails into their own fields. See ticket
  // #1680 https://github.com/department-of-veterans-affairs/abd-vro/issues/1680
}
