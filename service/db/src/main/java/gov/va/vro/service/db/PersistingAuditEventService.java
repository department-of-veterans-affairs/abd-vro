package gov.va.vro.service.db;

import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.persistence.repository.AuditEventRepository;
import gov.va.vro.service.db.mapper.AuditEventMapper;
import gov.va.vro.service.spi.audit.AuditEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersistingAuditEventService implements AuditEventService {

  private final AuditEventMapper mapper = Mappers.getMapper(AuditEventMapper.class);
  private final AuditEventRepository auditEventRepository;

  @Override
  public void logEvent(AuditEvent auditEvent) {
    log.info("Received audit event: " + auditEvent);
    auditEventRepository.save(mapper.toEntity(auditEvent));
  }
}
