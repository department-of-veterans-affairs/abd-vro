package gov.va.vro.service.db;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.db.mapper.ClaimMapper;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.services.FetchClaimsService;
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
  public List<Claim> fetchClaims() {
    List<ClaimEntity> claimList = claimRepository.findAll();
    List<Claim> claims = new ArrayList<>();
    for (ClaimEntity obj : claimList) {
      Claim claim = mapper.toClaim(obj);
      claims.add(claim);
    }

    return claims;
  }
}
