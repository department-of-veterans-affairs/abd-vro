package gov.va.vro.service.rrd.db.mapper;

import gov.va.vro.model.rrd.event.AuditEvent;
import gov.va.vro.persistence.model.AuditEventEntity;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditEventMapper {

  AuditEventEntity toEntity(AuditEvent auditEvent);

  default String className(Class<?> source) {
    return source.getSimpleName();
  }

  default String exceptionTrace(Throwable throwable) {
    return throwable == null ? null : ExceptionUtils.getStackTrace(throwable);
  }
}
