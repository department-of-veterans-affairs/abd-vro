package gov.va.vro.service.spi.audit;

import gov.va.vro.model.event.AuditEvent;

public interface AuditEventService {

  void logEvent(AuditEvent auditEvent);
}
