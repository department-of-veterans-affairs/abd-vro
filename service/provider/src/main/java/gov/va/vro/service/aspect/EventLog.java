package gov.va.vro.service.aspect;

import gov.va.vro.model.event.AuditEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
public class EventLog {

  private final List<AuditEvent> events = new ArrayList<>();

  public void logEvent(AuditEvent event) {
    events.add(event);
  }
}
