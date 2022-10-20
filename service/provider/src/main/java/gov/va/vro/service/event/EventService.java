package gov.va.vro.service.event;

import gov.va.vro.model.event.AuditEvent;

import java.util.List;

public interface EventService {

    void logEvent(AuditEvent event);

    List<AuditEvent> getEvents(String eventId);
}
