package gov.va.vro.persistence.repository;

import gov.va.vro.persistence.model.AssessmentResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResultEntity, UUID> {}
