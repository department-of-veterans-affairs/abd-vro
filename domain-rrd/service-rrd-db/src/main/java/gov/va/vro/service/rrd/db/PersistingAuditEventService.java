package gov.va.vro.service.rrd.db;

import gov.va.vro.model.rrd.event.AuditEvent;
import gov.va.vro.persistence.repository.AuditEventRepository;
import gov.va.vro.service.rrd.db.mapper.AuditEventMapper;
import gov.va.vro.service.spi.audit.AuditEventService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersistingAuditEventService implements AuditEventService {

  private final AuditEventMapper mapper = Mappers.getMapper(AuditEventMapper.class);
  private final AuditEventRepository auditEventRepository;

  @Override
  public void logEvent(AuditEvent auditEvent) {
    auditEventRepository.save(mapper.toEntity(auditEvent));
  }
}
