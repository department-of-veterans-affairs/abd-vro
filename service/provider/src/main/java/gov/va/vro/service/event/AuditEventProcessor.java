package gov.va.vro.service.event;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.event.Auditable;
import gov.va.vro.service.spi.audit.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditEventProcessor {

  private final EventService eventService;

  public Object logEvent(Object o, String routeId) {
    if (!(o instanceof Auditable auditableObject)) {
      // object cannot be audited
      return o;
    }
    AuditEvent event =
        AuditEvent.builder()
            .eventId(auditableObject.getEventId())
            .payloadType(auditableObject.getClass())
            .routeId(routeId)
            .build();
    eventService.logEvent(event);
    return o;
  }

  public void logException(Object o, Throwable t, String routeId) {
    if (!(o instanceof Auditable auditableObject)) {
      // object cannot be audited
      return;
    }
    AuditEvent event =
        AuditEvent.builder()
            .eventId(auditableObject.getEventId())
            .payloadType(auditableObject.getClass())
            .exception(t)
            .routeId(routeId)
            .build();
    eventService.logEvent(event);
  }

  public FunctionProcessor<Object, Object> eventProcessor(String routeId) {
    return FunctionProcessor.fromFunction(
        payload -> {
          logEvent(payload, routeId);
          return payload;
        });
  }
}
