package gov.va.vro.persistence.repository;

import gov.va.vro.persistence.model.ExamOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamOrderRepository extends JpaRepository<ExamOrderEntity, UUID> {

  Optional<ExamOrderEntity> findByCollectionId(String collectionId);

  Page<ExamOrderEntity> findByOrderedAtIsNull(Pageable params);
}
