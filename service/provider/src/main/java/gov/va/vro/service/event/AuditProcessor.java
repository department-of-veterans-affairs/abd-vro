package gov.va.vro.service.event;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.event.AuditableObject;
import gov.va.vro.model.event.EventProcessingType;
import gov.va.vro.model.event.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditProcessor {
  private final EventService eventService;

  public Object logEvent(Object o, EventType eventType, EventProcessingType processingType) {
    if (!(o instanceof AuditableObject)) {
      // object cannot be audited
      return o;
    }
    AuditableObject auditableObject = (AuditableObject) o;
    AuditEvent event =
        AuditEvent.builder()
            .eventId(auditableObject.getEventId())
            .payloadType(auditableObject.getClass())
            .eventType(eventType)
            .processingType(processingType)
            .build();
    eventService.logEvent(event);
    return o;
  }

  public FunctionProcessor<Object, Object> eventProcessor(
      EventType eventType, EventProcessingType eventProcessingType) {
    return FunctionProcessor.fromFunction(
        payload -> {
          logEvent(payload, eventType, eventProcessingType);
          return payload;
        });
  }
}
