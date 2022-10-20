package gov.va.vro.model.event;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder(toBuilder = true)
public class AuditEvent {

  private String eventId;
  private EventProcessingType processingType;
  private EventType eventType;
  private Class<?> payloadType;
  @Builder.Default
  private ZonedDateTime eventTime = ZonedDateTime.now();
  private Throwable exception;
}
