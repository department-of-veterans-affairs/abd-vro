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
  public void insertAssessmentResult(UUID claimId, String evidenceResponse, String diagnosticCode)
      throws IllegalArgumentException {
    ClaimEntity claimEntity =
        claimRepository
            .findById(claimId)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Could not match claim ID {" + claimId.toString() + "} in DB."));
    Map<String, Object> evidence = new HashMap<>();
    try {
      evidence = objMapper.readValue(evidenceResponse, Map.class);
      if (evidence.isEmpty()) {
        log.error(
            "Could not insert evidence summary into assessment result: evidenceSummary was empty.");
        evidence.put("evidenceSummary", "Could not insert evidence summary into assessment result");
      }
    } catch (Exception e) {
      log.error("Could not map evidence summary into assessment result.");
      evidence.put("evidenceSummary", "Could not insert evidence summary into assessment result");
    }
    Map summary = (Map) evidence.get("evidenceSummary");
    AssessmentResultEntity assessmentResultEntity = new AssessmentResultEntity();
    assessmentResultEntity.setEvidenceCountSummary(summary);
    ContentionEntity contention = findContention(claimEntity, diagnosticCode);
    contention.addAssessmentResult(assessmentResultEntity);
    claimRepository.save(claimEntity);
  }

  private ContentionEntity findContention(ClaimEntity claim, String diagnosticCode) {
    ContentionEntity resp = new ContentionEntity();
    for (ContentionEntity contention : claim.getContentions()) {
      if (contention.getDiagnosticCode().equals(diagnosticCode)) {
        resp = contention;
      } else {
        log.error("Could not find match contention with diagnostic code.");
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
