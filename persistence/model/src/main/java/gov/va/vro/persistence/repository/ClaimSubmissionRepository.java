package gov.va.vro.persistence.repository;

import gov.va.vro.persistence.model.ClaimSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimSubmissionRepository extends JpaRepository<ClaimSubmissionEntity, UUID> {

  Optional<ClaimSubmissionEntity> findByReferenceIdAndIdType(String referenceId, String idType);
}
