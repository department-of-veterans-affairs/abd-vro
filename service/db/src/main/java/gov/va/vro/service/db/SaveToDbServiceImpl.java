package gov.va.vro.service.db;

import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import gov.va.vro.persistence.repository.AssessmentResultRepository;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import gov.va.vro.service.db.mapper.ClaimMapper;
import gov.va.vro.service.spi.db.SaveToDbService;
import gov.va.vro.service.spi.model.AssessmentResult;
import gov.va.vro.service.spi.model.Claim;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveToDbServiceImpl implements SaveToDbService {

  private final VeteranRepository veteranRepository;
  private final ClaimRepository claimRepository;

  private final AssessmentResultRepository assessmentResultRepository;

  private final ClaimMapper mapper;

  @Override
  public Claim insertClaim(Claim claim) {
    VeteranEntity veteranEntity = findOrCreateVeteran(claim.getVeteranIcn());
    ClaimEntity entity =
        claimRepository
            .findByClaimSubmissionIdAndIdType(claim.getClaimSubmissionId(), claim.getIdType())
            .orElseGet(() -> createClaim(claim, veteranEntity));
    claim.setRecordId(entity.getId());
    return claim;
  }

  @Override
  public AssessmentResult insertAssessmentResult(AssessmentResult assessmentResult){
    AssessmentResultEntity arEntity = new AssessmentResultEntity();
    arEntity.setEvidenceCount(assessmentResult.getEvidenceCount());
    String evidenceSummary = assessmentResult.getEvidenceSummary().toString();
    arEntity.setEvidenceSummary(evidenceSummary);
    assessmentResultRepository.save(arEntity);
    assessmentResult.setId(arEntity.getId());
    return assessmentResult;
  }

  private ClaimEntity createClaim(Claim claim, VeteranEntity veteranEntity) {
    ClaimEntity claimEntity = mapper.toClaimEntity(claim);
    claimEntity.setVeteran(veteranEntity);
    ContentionEntity contentionEntity = new ContentionEntity();
    contentionEntity.setDiagnosticCode(claim.getDiagnosticCode());
    claimEntity.addContention(contentionEntity);
    return claimRepository.save(claimEntity);
  }

  private VeteranEntity findOrCreateVeteran(String veteranIcn) {
    return veteranRepository.findByIcn(veteranIcn).orElseGet(() -> createVeteran(veteranIcn));
  }

  private VeteranEntity createVeteran(String veteranIcn) {
    VeteranEntity veteranEntity = new VeteranEntity();
    veteranEntity.setIcn(veteranIcn);
    return veteranRepository.save(veteranEntity);
  }
}
