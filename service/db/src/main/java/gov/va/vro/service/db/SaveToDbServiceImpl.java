package gov.va.vro.service.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import gov.va.vro.service.db.mapper.ClaimMapper;
import gov.va.vro.service.spi.db.SaveToDbService;
import gov.va.vro.service.spi.model.AssessmentResult;
import gov.va.vro.service.spi.model.Claim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveToDbServiceImpl implements SaveToDbService {

  private final VeteranRepository veteranRepository;
  private final ClaimRepository claimRepository;
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
  public AssessmentResult insertAssessmentResult(
      UUID id,
      String assessmentResult,
      String veteranIcn,
      String claimSubmissionId,
      String idType,
      String diagnosticCode)
      throws NoSuchFieldException {
    ClaimEntity claimEntity = claimRepository.findById(id).orElse(null);
    if (claimEntity == null) {
      throw new NoSuchFieldException("Could not match claim ID {" + id.toString() + "} in DB.");
    }
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> evidence = new HashMap<>();
    try {
      evidence = mapper.readValue(assessmentResult, Map.class);
    } catch (Exception e) {
      log.error("Could not map assessmentResult");
    }
    Map summary = (Map) evidence.getOrDefault("evidenceSummary", null);
    int count = 0;
    if (summary != null) {
      for (Object value : summary.values()) {
        count += (Integer) value;
      }
    }
    AssessmentResultEntity assessmentResultEntity = new AssessmentResultEntity();
    assessmentResultEntity.setEvidenceCount(count);
    assessmentResultEntity.setEvidenceCountSummary(summary);
    for (ContentionEntity contention : claimEntity.getContentions()) {
      if ((contention.getDiagnosticCode().equals(diagnosticCode))
          && (contention.getClaim().getId() == id)) {
        for (AssessmentResultEntity ar : contention.getAssessmentResults())
          if (ar.getContention().getId() == contention.getId()) {
            log.info("Assessment result already populated, continuing.");
            break;
          }
        contention.addAssessmentResult(assessmentResultEntity);
        claimRepository.save(claimEntity);
      } else {
        String msg = "Could not match contention with diagnosticCode and claimId";
        log.error(msg);
      }
    }
    AssessmentResult resp = new AssessmentResult();
    resp.setEvidenceSummary(assessmentResultEntity.getEvidenceCountSummary());
    resp.setEvidenceCount(assessmentResultEntity.getEvidenceCount());
    resp.setVeteranIcn(veteranIcn);
    resp.setDiagnosticCode(diagnosticCode);
    return resp;
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
