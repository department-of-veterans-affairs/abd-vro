package gov.va.vro.model.event;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class AuditEvent {

  private String eventId;
  private EventType eventType;
  private String qualifier; // TODO enum
  private ZonedDateTime eventTime;
  private String message;
}
