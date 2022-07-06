package gov.va.vro.service.db;

import gov.va.starter.example.service.spi.db.SaveToDbService;
import gov.va.starter.example.service.spi.db.model.Claim;
import gov.va.starter.example.service.spi.db.model.Veteran;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import gov.va.vro.service.db.mapper.ClaimRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SaveToDbServiceImpl implements SaveToDbService {

  private final VeteranRepository veteranRepository;
  private final ClaimRepository claimRepository;
  private final ClaimRequestMapper mapper;

  @Override
  public void persistClaim(Claim claim) {
    VeteranEntity veteranEntity = findOrCreateVeteran(claim.getVeteran());
    Optional<ClaimEntity> existingClaim =
        claimRepository.findByClaimIdAndIdType(claim.getClaimId(), claim.getIdType());
    if (existingClaim.isPresent()) {
      return;
    }
    createClaim(claim, veteranEntity);
  }

  private ClaimEntity createClaim(Claim claim, VeteranEntity veteranEntity) {
    ClaimEntity claimEntity = mapper.toClaimEntity(claim);
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
