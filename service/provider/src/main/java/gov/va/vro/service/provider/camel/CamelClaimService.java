package gov.va.vro.service.provider.camel;

import gov.va.starter.example.service.spi.claimsubmission.ClaimSubmissionService;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CamelClaimService {
  private final ClaimSubmissionService claimSubmissionService;

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
