package gov.va.starter.example.persistence.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

@Deprecated // demo code
public interface ClaimSubmissionEntityRepository
    extends PagingAndSortingRepository<ClaimSubmissionEntity, String> {

  Optional<ClaimSubmissionEntity> findByUserName(String userName);

  Page<ClaimSubmissionEntity> findByLastName(String lastName, Pageable pageable);
}
