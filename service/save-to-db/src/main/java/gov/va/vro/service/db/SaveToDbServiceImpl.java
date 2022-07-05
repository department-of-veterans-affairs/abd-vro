package gov.va.vro.service.db;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import gov.va.vro.service.db.mapper.ClaimRequestMapper;
import gov.va.vro.service.db.model.ClaimRequest;
import gov.va.vro.service.db.model.Veteran;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveToDbServiceImpl {

  private final VeteranRepository veteranRepository;
  private final ClaimRepository claimRepository;

  private final ClaimRequestMapper mapper;

  public void persistClaim(ClaimRequest claimRequest) {
    VeteranEntity veteranEntity = findOrCreateVeteran(claimRequest.getVeteran());
    // TODO: for now, assume there cannot be an existing claim
    createClaim(claimRequest, veteranEntity);
  }

  private ClaimEntity createClaim(ClaimRequest claimRequest, VeteranEntity veteranEntity) {
    ClaimEntity claimEntity = mapper.toClaimEntity(claimRequest);
    claimEntity.setVeteran(veteranEntity);
    return claimRepository.save(claimEntity);
  }

  private VeteranEntity findOrCreateVeteran(Veteran veteran) {
    return veteranRepository.findByIcn(veteran.getIcn()).orElseGet(() -> createVeteran(veteran));
  }

  private VeteranEntity createVeteran(Veteran veteran) {
    VeteranEntity veteranEntity = mapper.toVeteranEntity(veteran);
    return veteranRepository.save(veteranEntity);
  }
}
