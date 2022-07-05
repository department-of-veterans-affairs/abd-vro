package gov.va.vro.service.db;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import gov.va.vro.service.db.model.ClaimRequest;
import gov.va.vro.service.db.model.Contention;
import gov.va.vro.service.db.model.Veteran;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveToDbServiceImpl {

  private final VeteranRepository veteranRepository;
  private final ClaimRepository claimRepository;

  public void persistClaim(ClaimRequest claimRequest) {
    VeteranEntity veteranEntity = findOrCreateVeteran(claimRequest.getVeteran());
    // TODO: for now, assume there cannot be an existing claim
    createClaim(claimRequest, veteranEntity);
  }

  private ClaimEntity createClaim(ClaimRequest claimRequest, VeteranEntity veteranEntity) {
    // TODO: use Mapper
    ClaimEntity claimEntity = new ClaimEntity();
    claimEntity.setVeteran(veteranEntity);
    claimEntity.setClaimId(claimRequest.getClaimId());
    claimEntity.setIdType(claimRequest.getIdType());
    claimEntity.setIncomingStatus(claimRequest.getIncomingStatus());
    claimRequest.getContentions().stream()
        .map(this::createContention)
        .forEach(claimEntity::addContention);
    return claimRepository.save(claimEntity);
  }

  private ContentionEntity createContention(Contention contention) {
    ContentionEntity contentionEntity = new ContentionEntity();
    contentionEntity.setDiagnosticCode(contention.getDiagnosticCode());
    // TODO: assessments and evidence
    return contentionEntity;
  }

  private VeteranEntity findOrCreateVeteran(Veteran veteran) {
    return veteranRepository.findByIcn(veteran.getIcn()).orElseGet(() -> createVeteran(veteran));
  }

  private VeteranEntity createVeteran(Veteran veteran) {
    VeteranEntity veteranEntity = new VeteranEntity();
    veteranEntity.setParticipantId(veteran.getParticipantId());
    veteranEntity.setIcn(veteran.getIcn());
    return veteranRepository.save(veteranEntity);
  }
}
