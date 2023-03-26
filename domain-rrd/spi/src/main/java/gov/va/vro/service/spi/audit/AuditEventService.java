package gov.va.vro.service.spi.audit;

import gov.va.vro.model.event.AuditEvent;

/**
 * Service responsible for logging an audit event using some communication channel or persistence
 * mechanism such as database, queue, slack channel, etc.
 */
public interface AuditEventService {

  void logEvent(AuditEvent auditEvent);
}
