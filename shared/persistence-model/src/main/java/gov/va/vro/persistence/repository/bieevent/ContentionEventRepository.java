package gov.va.vro.persistence.repository.bieevent;

import gov.va.vro.persistence.model.bieevents.ContentionEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContentionEventRepository extends JpaRepository<ContentionEventEntity, UUID> {}
