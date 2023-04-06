package gov.va.vro.persistence.repository;

import gov.va.vro.persistence.model.ClaimEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<ClaimEntity, UUID> {

  Optional<ClaimEntity> findByVbmsId(String vbmsId);

  Page<ClaimEntity> findAllByVeteranIcn(String veteranIcn, PageRequest pageRequest);
}
