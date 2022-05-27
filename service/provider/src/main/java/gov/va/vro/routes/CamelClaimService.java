package gov.va.vro.routes;

import gov.va.starter.example.service.spi.claimsubmission.ClaimSubmissionService;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CamelClaimService {
  @Autowired private ClaimSubmissionService claimSubmissionService;

  public List<ClaimSubmission> getAllClaims() {
    return claimSubmissionService.findAll(Pageable.unpaged()).stream().collect(Collectors.toList());
  }

  public ClaimSubmission addClaim(ClaimSubmission claimSubmission) {
    return claimSubmissionService.add(claimSubmission);
  }

  public ClaimSubmission getClaim(String id) {
    return claimSubmissionService.findById(id).get();
  }

  public ClaimSubmission claimDetail(Exchange exchange) {
    String id = exchange.getIn().getHeader("id").toString();
    return claimSubmissionService.findById(id).get();
  }
}
