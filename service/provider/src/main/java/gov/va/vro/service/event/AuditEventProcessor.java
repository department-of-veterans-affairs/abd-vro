package gov.va.vro.service.event;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.event.Auditable;
import gov.va.vro.service.spi.audit.AuditEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Camel-oriented processor responsible for capturing audit events and forwarding them to the
 * AuditEventService
 */
@Component
@RequiredArgsConstructor
public class AuditEventProcessor {

  private final AuditEventService auditEventService;

  public Object logEvent(Object o, String routeId, String message) {
    if (!(o instanceof Auditable auditableObject)) {
      // object cannot be audited
      return o;
    }
    AuditEvent event = builder(auditableObject, routeId).message(message).build();
    auditEventService.logEvent(event);
    return o;
  }

  public Object logEvent(
      Object o, String routeId, String message, Function<Auditable, String> detailsExtractor) {
    if (!(o instanceof Auditable auditableObject)) {
      // object cannot be audited
      return o;
    }
    AuditEvent event =
        builder(auditableObject, routeId)
            .message(message)
            .details(detailsExtractor.apply(auditableObject))
            .build();
    auditEventService.logEvent(event);
    return o;
  }

  public void logException(Object o, Throwable t, String routeId) {
    if (!(o instanceof Auditable auditableObject)) {
      // object cannot be audited
      return;
    }
    AuditEvent event = builder(auditableObject, routeId).throwable(t).build();
    auditEventService.logEvent(event);
  }

  private AuditEvent.AuditEventBuilder builder(Auditable auditableObject, String routeId) {
    return AuditEvent.builder()
        .eventId(auditableObject.getEventId())
        .payloadType(auditableObject.getClass())
        .routeId(routeId);
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
