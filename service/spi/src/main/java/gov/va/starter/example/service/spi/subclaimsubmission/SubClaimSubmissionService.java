package gov.va.starter.example.service.spi.subclaimsubmission;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.starter.example.service.spi.subclaimsubmission.model.SubClaimSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Deprecated
public interface SubClaimSubmissionService {

  SubClaimSubmission add(SubClaimSubmission resource) throws RequestValidationException;

  Page<SubClaimSubmission> findByLastName(String lastName, Pageable pageable);

  Optional<SubClaimSubmission> findByUserName(String userName);

  Optional<SubClaimSubmission> findById(String id);

  Page<SubClaimSubmission> findAll(Pageable pageable);

  Optional<SubClaimSubmission> updateById(String id, SubClaimSubmission record)
      throws RequestValidationException;

  Optional<SubClaimSubmission> deleteById(String id);
}
