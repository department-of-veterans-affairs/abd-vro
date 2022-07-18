package gov.va.starter.example.persistence.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface SubClaimSubmissionEntityRepository
    extends PagingAndSortingRepository<SubClaimSubmissionEntity, String> {

  Optional<SubClaimSubmissionEntity> findByUserName(String userName);

  Page<SubClaimSubmissionEntity> findByLastName(String lastName, Pageable pageable);

  Page<SubClaimSubmissionEntity> findAllByClaimSubmissionId(
      String claimSubmissionId, Pageable pageable);
}
