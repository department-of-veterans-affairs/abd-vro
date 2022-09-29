package gov.va.vro.persistence.repository;

import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssessmentResultRepository extends JpaRepository<AssessmentResultEntity, UUID> {

}
