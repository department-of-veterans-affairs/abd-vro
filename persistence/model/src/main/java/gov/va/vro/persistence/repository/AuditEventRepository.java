package gov.va.vro.persistence.repository;

import gov.va.vro.persistence.model.AuditEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEventEntity, UUID> {

  List<AuditEventEntity> findByEventIdOrderByEventTimeAsc(String eventId);
}
