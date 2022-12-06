package gov.va.vro.persistence.repository;

import gov.va.vro.persistence.model.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<ClaimEntity, UUID> {
  Optional<ClaimEntity> findByClaimSubmissionIdAndIdType(String claimSubmissionId, String idType);

  Optional<ClaimEntity> findByClaimSubmissionId(String claimSubmissionId);

  List<ClaimEntity> findAllByVeteranIcn(String veteranIcn);
}
