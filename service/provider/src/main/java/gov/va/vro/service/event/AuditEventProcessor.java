package gov.va.vro.service.event;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.event.Auditable;
import gov.va.vro.service.spi.audit.AuditEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditEventProcessor {

  private final AuditEventService auditEventService;

  public Object logEvent(Object o, String routeId, String message) {
    if (!(o instanceof Auditable auditableObject)) {
      // object cannot be audited
      return o;
    }
    AuditEvent event =
        AuditEvent.builder()
            .eventId(auditableObject.getEventId())
            .payloadType(auditableObject.getClass())
            .routeId(routeId)
            .message(message)
            .build();
    auditEventService.logEvent(event);
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
            .throwable(t)
            .routeId(routeId)
            .build();
    auditEventService.logEvent(event);
  }

  /**
   * Create a Camel processor that logs an event
   *
   * @param routeId the id of the route
   * @param message a message to report with the event
   */
  public FunctionProcessor<Object, Object> event(String routeId, String message) {
    return FunctionProcessor.fromFunction(
        payload -> {
          logEvent(payload, routeId, message);
          return payload;
        });
  }
}
