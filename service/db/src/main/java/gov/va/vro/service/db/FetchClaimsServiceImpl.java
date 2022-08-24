package gov.va.vro.service.db;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.db.mapper.ClaimMapper;
import gov.va.vro.service.spi.model.SimpleClaim;
import gov.va.vro.service.spi.services.fetchclaims.FetchClaimsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FetchClaimsServiceImpl implements FetchClaimsService {

  private final ClaimRepository claimRepository;
  private final ClaimMapper mapper;

  @Override
  public List<SimpleClaim> fetchClaims() {
    List<ClaimEntity> claimList = new ArrayList<>();
    claimList = claimRepository.findAll();
    List<SimpleClaim> simpleClaims = new ArrayList<>();

    for (ClaimEntity claimEntity : claimList) {
      SimpleClaim claim = new SimpleClaim();
      claim.setClaimSubmissionId(claimEntity.getClaimSubmissionId());
      claim.setVeteranIcn(claimEntity.getVeteran().getIcn());
      List<String> contentionList = new ArrayList<>();
      for (int i = 0; i < claimEntity.getContentions().size(); i++) {
        String contention = (claimEntity.getContentions().get(i).getDiagnosticCode());
        contentionList.add(contention);
      }
      claim.setContentions(contentionList);
      simpleClaims.add(claim);
    }

    return simpleClaims;
  }
}
