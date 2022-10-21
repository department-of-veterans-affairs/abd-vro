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
  private final ObjectMapper objMapper;

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
      UUID id, String assessmentResult, String veteranIcn, String diagnosticCode)
      throws NoSuchFieldException {
    ClaimEntity claimEntity =
        claimRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new NoSuchFieldException(
                        "Could not match claim ID {" + id.toString() + "} in DB."));
    Map<String, Object> evidence = new HashMap<>();
    try {
      evidence = objMapper.readValue(assessmentResult, Map.class);
      if (evidence.isEmpty()) {
        throw new NoSuchFieldException("Could not map assesmentResult, evidence summary is null.");
      }
    } catch (Exception e) {
      log.error("Could not map assesmentResult, evidence summary is null.");
    }
    Map<String, Object> defaultEvidence = new HashMap<>();
    defaultEvidence.put("medicationsCount", 0);
    Map summary = (Map) evidence.getOrDefault("evidenceSummary", defaultEvidence);
    Object code = evidence.get("diagnosticCode");
    String dcode = (String) code;
    AssessmentResultEntity assessmentResultEntity = new AssessmentResultEntity();
    assessmentResultEntity.setEvidenceCountSummary(summary);
    ContentionEntity contention = findContention(claimEntity, diagnosticCode);
    contention.addAssessmentResult(assessmentResultEntity);
    claimRepository.save(claimEntity);
    AssessmentResult resp = new AssessmentResult();
    resp.setEvidenceSummary(assessmentResultEntity.getEvidenceCountSummary());
    resp.setVeteranIcn(veteranIcn);
    resp.setDiagnosticCode(diagnosticCode);
    return resp;
  }

  private ContentionEntity findContention(ClaimEntity claim, String diagnosticCode)
      throws NoSuchFieldException {
    ContentionEntity resp = new ContentionEntity();
    for (ContentionEntity contention : claim.getContentions()) {
      if (contention.getDiagnosticCode().equals(diagnosticCode)) {
        resp = contention;
      } else {
        log.error("Could not find match contention with diagnostic code.");
        throw new NoSuchFieldException("Could not find match contention with diagnostic code.");
      }
    }
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
