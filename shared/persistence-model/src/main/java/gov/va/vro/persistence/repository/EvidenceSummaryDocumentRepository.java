package gov.va.vro.persistence.repository;

import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EvidenceSummaryDocumentRepository
    extends JpaRepository<EvidenceSummaryDocumentEntity, UUID> {

  Optional<EvidenceSummaryDocumentEntity> findFirstByContentionIdOrderByCreatedAtDesc(UUID uuid);
}
