package gov.va.vro.service.event;

import gov.va.vro.model.event.AuditEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Dummy service for demo purposes */
@Component
public class ListEventService implements EventService {

  private final List<AuditEvent> events = new ArrayList<>();

  @Override
  public void logEvent(AuditEvent event) {
    events.add(event);
  }

  @Override
  public List<AuditEvent> getEvents(String eventId) {
    return events.stream()
        .filter(event -> event.getEventId().equals(eventId))
        .collect(Collectors.toList());
  }
}
