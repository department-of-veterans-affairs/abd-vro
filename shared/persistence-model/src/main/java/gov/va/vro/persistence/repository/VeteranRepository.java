package gov.va.vro.persistence.repository;

import gov.va.vro.persistence.model.VeteranEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VeteranRepository extends JpaRepository<VeteranEntity, UUID> {

  Optional<VeteranEntity> findByIcn(String icn);
}
