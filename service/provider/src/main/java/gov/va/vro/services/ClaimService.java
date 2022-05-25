package gov.va.vro.services;

import gov.va.starter.example.persistence.model.ClaimSubmissionEntity;
import gov.va.starter.example.persistence.model.ClaimSubmissionEntityRepository;
import gov.va.vro.model.ClaimStatus;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClaimService {
  @Autowired private ClaimSubmissionEntityRepository claimRepository;

  public List<ClaimSubmissionEntity> getAllClaims() {
    List<ClaimSubmissionEntity> claimEntityRecords = new ArrayList<>();
    claimRepository.findAll().forEach(claimEntityRecords::add);
    return claimEntityRecords;
  }

  public ClaimSubmissionEntity addClaim(ClaimSubmissionEntity claimEntityRecord) {
    return claimRepository.save(claimEntityRecord);
  }

  public ClaimSubmissionEntity getClaim(String id) {
    return claimRepository.findById(id).get();
  }

  public ClaimSubmissionEntity claimDetail(Exchange exchange) {
    String id = exchange.getIn().getHeader("id").toString();
    return getClaim(id);
  }

  public ClaimSubmissionEntity updateStatus(String id, ClaimStatus status) {
    ClaimSubmissionEntity claim = getClaim(id);
    claim.setStatus(status);
    claimRepository.save(claim);
    return claim;
  }
}
