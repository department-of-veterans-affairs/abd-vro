package gov.va.vro.service.event;

import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.service.spi.audit.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Temporary class until proper service is implemented */
@Service
@Slf4j
public class LoggingEventService implements EventService {

  @Override
  public void logEvent(AuditEvent event) {
    log.info(event.toString());
  }
}
