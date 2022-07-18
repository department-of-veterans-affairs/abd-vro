package gov.va.starter.example.service.spi.claimsubmission;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.starter.example.service.spi.claimsubmission.model.SubClaimSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Deprecated // part of prototype
public interface ClaimSubmissionService {

  ClaimSubmission add(ClaimSubmission resource) throws RequestValidationException;

  Page<ClaimSubmission> findByLastName(String lastName, Pageable pageable);

  Optional<ClaimSubmission> findByUserName(String userName);

  Optional<ClaimSubmission> findById(String id);

  Page<ClaimSubmission> findAll(Pageable pageable);

  Optional<ClaimSubmission> updateById(String id, ClaimSubmission record)
      throws RequestValidationException;

  Optional<ClaimSubmission> updateStatusById(String id, ClaimSubmission.ClaimStatus status);

  Optional<ClaimSubmission> deleteById(String id);

  SubClaimSubmission addSubClaimSubmission(String id, SubClaimSubmission subResource)
      throws RequestValidationException;

  Page<SubClaimSubmission> getSubClaimSubmissions(String id, Pageable pageable);

  Optional<SubClaimSubmission> getSubClaimSubmission(String id, String subResourceId);

  // CSOFF: LineLength
  Optional<SubClaimSubmission> updateSubClaimSubmission(
      String id, String subResourceId, SubClaimSubmission subResource)
      throws RequestValidationException;
  // CSON: LineLength

  Optional<SubClaimSubmission> deleteSubClaimSubmission(String id, String subResourceId);
}
