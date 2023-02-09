package gov.va.vro.service.db;

import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ClaimSubmissionEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.ExamOrderEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import gov.va.vro.persistence.model.VeteranFlashIdEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.ClaimSubmissionRepository;
import gov.va.vro.persistence.repository.ExamOrderRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import gov.va.vro.service.db.mapper.ClaimMapper;
import gov.va.vro.service.spi.db.SaveToDbService;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.ExamOrder;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveToDbServiceImpl implements SaveToDbService {

  private final VeteranRepository veteranRepository;
  private final ClaimRepository claimRepository;

  private final ClaimSubmissionRepository claimSubmissionRepository;
  private final ExamOrderRepository examOrderRepository;
  private final ClaimMapper mapper;

  @Override
  @Transactional
  public Claim insertClaim(Claim claim) {
    VeteranEntity veteranEntity = findOrCreateVeteran(claim.getVeteranIcn());
    ClaimSubmissionEntity claimSubmissionEntity = createClaimSubmission(claim);
    ClaimEntity claimEntity =
        claimRepository
            .findByVbmsId(claim.getClaimSubmissionId())
            .orElseGet(() -> createClaim(claim, veteranEntity));
    ensureContentionExists(claimEntity, claim.getDiagnosticCode());
    claimEntity.addClaimSubmission(claimSubmissionEntity);
    claimSubmissionRepository.save(claimSubmissionEntity);
    claimRepository.save(claimEntity);
    claim.setRecordId(claimEntity.getId());
    return claim;
  }

  private ClaimSubmissionEntity createClaimSubmission(Claim claim) {
    ClaimSubmissionEntity claimSubmission = new ClaimSubmissionEntity();
    claimSubmission.setReferenceId(claim.getClaimSubmissionId());
    claimSubmission.setIdType(claim.getIdType());
    claimSubmission.setIncomingStatus(claim.getIncomingStatus());
    claimSubmission.setSubmissionSource(claim.getSubmissionSource());
    claimSubmission.setSubmissionDate(claim.getSubmissionDate());
    claimSubmission.setOffRampReason(claim.getOffRampReason());
    return claimSubmission;
  }

  @Override
  public void insertAssessmentResult(
      UUID claimId, AbdEvidenceWithSummary evidenceResponse, String diagnosticCode) {
    ClaimEntity claimEntity = claimRepository.findById(claimId).orElse(null);
    if (claimEntity == null) {
      log.warn("Could not match Claim ID in insertAssessmentResult, exiting.");
      return;
    }
    insertAssessmentResult(claimEntity, evidenceResponse, diagnosticCode);
  }

  @Override
  public void insertAssessmentResult(AbdEvidenceWithSummary evidence, String diagnosticCode) {
    var claimEntity = claimRepository.findByVbmsId(evidence.getClaimSubmissionId());
    if (claimEntity.isEmpty()) {
      log.warn(
          "Claim not found for claimEntity submission id = {} and id type = {}",
          evidence.getClaimSubmissionId(),
          Claim.DEFAULT_ID_TYPE);
      return;
    }
    insertAssessmentResult(claimEntity.get(), evidence, diagnosticCode);
  }

  private void insertAssessmentResult(
      ClaimEntity claimEntity, AbdEvidenceWithSummary evidenceResponse, String diagnosticCode) {
    Map<String, String> summary = convertMap(evidenceResponse.getEvidenceSummary());
    if (summary == null || summary.isEmpty()) {
      log.warn("Evidence Summary is empty, exiting.");
      return;
    }
    AssessmentResultEntity assessmentResultEntity = new AssessmentResultEntity();
    assessmentResultEntity.setEvidenceCountSummary(summary);
    ContentionEntity contention = findContention(claimEntity, diagnosticCode);
    if (contention == null) {
      log.warn("Could not match contention with diagnostic code");
      return;
    }
    contention.addAssessmentResult(assessmentResultEntity);
    claimRepository.save(claimEntity);
  }

  @Override
  public void insertEvidenceSummaryDocument(GeneratePdfPayload request, String documentName) {
    ClaimEntity claim = claimRepository.findByVbmsId(request.getClaimSubmissionId()).orElse(null);
    if (claim == null) {
      log.warn("Could not find claim by claimSubmissionId, exiting.");
      return;
    }
    ContentionEntity contention = findContention(claim, request.getDiagnosticCode());
    if (contention == null) {
      log.warn("Could not match the contention with the claim and diagnostic code, exiting.");
      return;
    }
    Map<String, String> evidenceCount = fillEvidenceCounts(request);
    contention.addEvidenceSummaryDocument(evidenceCount, documentName);
    claimRepository.save(claim);
  }

  @Override
  @Transactional
  public void insertOrUpdateExamOrderingStatus(ExamOrder examOrder) {
    ExamOrderEntity examOrderEntity =
        examOrderRepository
            .findByCollectionId(examOrder.getCollectionId())
            .orElseGet(() -> createExamOrder(examOrder));
    if (null != examOrderEntity) {
      examOrderEntity.setCollectionId(examOrder.getCollectionId());
      examOrderEntity.setStatus(examOrder.getStatus());
      examOrderRepository.save(examOrderEntity);
    }
  }

  @Override
  public void insertFlashIds(List<String> veteranFlashIds, String veteranIcn) {
    var veteran = veteranRepository.findByIcn(veteranIcn);
    if (veteran.isEmpty()) {
      log.warn("Could not find a Veteran with this ICN. Could not attach flash IDs.");
      return;
    }
    if (veteranFlashIds == null) {
      log.warn("The Veteran Flash ID list was null, could not attach to Veteran.");
      return;
    }
    VeteranEntity entity = veteran.get();
    List<VeteranFlashIdEntity> flashIdList = createFlashIds(veteranFlashIds, entity);
    entity.setFlashIds(flashIdList);
    veteranRepository.save(entity);
  }

  private List<VeteranFlashIdEntity> createFlashIds(
      List<String> veteranFlashIds, VeteranEntity entity) {
    List<VeteranFlashIdEntity> flashIdList = new ArrayList<>();
    for (String flashId : veteranFlashIds) {
      VeteranFlashIdEntity id = new VeteranFlashIdEntity();
      id.setFlashId(Integer.valueOf(flashId));
      id.setVeteran(entity);
      flashIdList.add(id);
    }
    return flashIdList;
  }

  private Map<String, String> fillEvidenceCounts(GeneratePdfPayload request) {
    AbdEvidence evidence = request.getEvidence();
    Map<String, String> evidenceCount = new HashMap<>();
    if (evidence.getBloodPressures() != null) {
      evidenceCount.put("totalBpReadings", String.valueOf(evidence.getBloodPressures().size()));
    }
    if (evidence.getMedications() != null) {
      evidenceCount.put("medicationsCount", String.valueOf(evidence.getMedications().size()));
    }
    if (evidence.getProcedures() != null) {
      evidenceCount.put("proceduresCount", String.valueOf(evidence.getProcedures().size()));
    }
    return evidenceCount;
  }

  private Map<String, String> convertMap(Map<String, Object> summary) {
    if (summary == null) {
      return null;
    }
    Map<String, String> result = new HashMap<>();
    for (Map.Entry<String, Object> entry : summary.entrySet()) {
      Object value = entry.getValue();
      if (value != null) {
        result.put(entry.getKey(), value.toString());
      }
    }
    return result;
  }

  private ContentionEntity ensureContentionExists(ClaimEntity claim, String diagnosticCode) {
    var contention = findContention(claim, diagnosticCode);
    if (contention == null) {
      contention = createContention(claim, diagnosticCode);
      claimRepository.save(claim);
    }
    return contention;
  }

  private ContentionEntity findContention(ClaimEntity claim, String diagnosticCode) {
    for (ContentionEntity contention : claim.getContentions()) {
      if (contention.getDiagnosticCode().equals(diagnosticCode)) {
        return contention;
      }
    }
    return null;
  }

  private ClaimEntity createClaim(Claim claim, VeteranEntity veteranEntity) {
    ClaimEntity claimEntity = mapper.toClaimEntity(claim);
    claimEntity.setVeteran(veteranEntity);
    createContention(claimEntity, claim.getDiagnosticCode());
    return claimRepository.save(claimEntity);
  }

  private ExamOrderEntity createExamOrder(ExamOrder examOrder) {
    ExamOrderEntity examOrderEntity = new ExamOrderEntity();
    examOrderEntity.setCollectionId(examOrder.getCollectionId());
    examOrderEntity.setStatus(examOrder.getStatus());
    return examOrderRepository.save(examOrderEntity);
  }

  private ContentionEntity createContention(ClaimEntity claim, String diagnosticCode) {
    ContentionEntity contentionEntity = new ContentionEntity();
    contentionEntity.setDiagnosticCode(diagnosticCode);
    claim.addContention(contentionEntity);
    return contentionEntity;
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
