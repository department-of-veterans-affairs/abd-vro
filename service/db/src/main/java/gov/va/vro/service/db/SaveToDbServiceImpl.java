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
import gov.va.vro.persistence.repository.AssessmentResultRepository;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveToDbServiceImpl implements SaveToDbService {

  private final VeteranRepository veteranRepository;
  private final ClaimRepository claimRepository;

  private final AssessmentResultRepository assessmentResultRepository;

  private final ClaimSubmissionRepository claimSubmissionRepository;
  private final ExamOrderRepository examOrderRepository;
  private final ClaimMapper mapper;

  @Override
  @Transactional
  public Claim insertClaim(Claim claim) {
    VeteranEntity veteranEntity = findOrCreateVeteran(claim.getVeteranIcn());
    ClaimEntity claimEntity = null;

    if (claim.getBenefitClaimId() == null) {
      // V1 endpoints do not give us a benefit claim id. They give us a claimSubmissionId (which is
      // stored as reference_id in claim_submission)
      Optional<ClaimSubmissionEntity> v1ClaimSubmission =
          claimSubmissionRepository.findFirstByReferenceIdAndIdTypeOrderByCreatedAtDesc(
              claim.getCollectionId(), claim.getIdType());
      if (v1ClaimSubmission.isPresent()) {
        claimEntity = v1ClaimSubmission.get().getClaim();
      } else {
        claimEntity = createClaim(claim, veteranEntity);
      }
    } else {
      // V2 endpoints go through here with a benefit claim id, and collectionId is the reference_id
      // on claimSubmission
      claimEntity =
          claimRepository
              .findByVbmsId(claim.getBenefitClaimId())
              .orElseGet(() -> createClaim(claim, veteranEntity));
    }

    ClaimSubmissionEntity claimSubmissionEntity = createClaimSubmission(claim);
    ensureContentionExists(claimEntity, claim.getDiagnosticCode());
    claimEntity.addClaimSubmission(claimSubmissionEntity);
    claimSubmissionRepository.save(claimSubmissionEntity);
    claimRepository.save(claimEntity);
    claim.setRecordId(claimEntity.getId());
    return claim;
  }

  private ClaimSubmissionEntity createClaimSubmission(Claim claim) {
    ClaimSubmissionEntity claimSubmission = new ClaimSubmissionEntity();
    // For v1 endpoints, PostClaimRequestMapper maps claimSubmissionId to collectionId
    claimSubmission.setReferenceId(claim.getCollectionId());
    claimSubmission.setIdType(claim.getIdType());
    claimSubmission.setIncomingStatus(claim.getIncomingStatus());
    claimSubmission.setSubmissionSource(claim.getSubmissionSource());
    claimSubmission.setSubmissionDate(claim.getSubmissionDate());
    claimSubmission.setOffRampReason(claim.getOffRampReason());
    claimSubmission.setInScope(claim.isInScope());
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
    // For v1 endpoints, PostClaimRequestMapper maps claimSubmissionId to collectionId
    Optional<ClaimSubmissionEntity> claimSubmission =
        claimSubmissionRepository.findFirstByReferenceIdAndIdTypeOrderByCreatedAtDesc(
            evidence.getClaimSubmissionId(), evidence.getIdType());
    if (claimSubmission.isEmpty()) {
      log.warn(
          "Claim Submission not found for claim submission id = {} and id type = {} to insert assessment result",
          evidence.getClaimSubmissionId(),
          evidence.getIdType());
      return;
    }
    ClaimEntity claimEntity = claimSubmission.get().getClaim();
    insertAssessmentResult(claimEntity, evidence, diagnosticCode);
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
  public void setOffRampReason(Claim claimWithOffRamp) {
    Optional<ClaimSubmissionEntity> claimSubmission =
        claimSubmissionRepository.findFirstByReferenceIdAndIdTypeOrderByCreatedAtDesc(
            String.valueOf(claimWithOffRamp.getCollectionId()), claimWithOffRamp.getIdType());
    ClaimSubmissionEntity claimSubmissionEntity = claimSubmission.get();
    claimSubmissionEntity.setOffRampReason(claimWithOffRamp.getOffRampReason());
    claimSubmissionRepository.save(claimSubmissionEntity);
  }

  @Override
  public void insertEvidenceSummaryDocument(GeneratePdfPayload request, String documentName) {
    // For v1 endpoints, ClaimSubmissionId = ClaimSubmission.referenceId
    Optional<ClaimSubmissionEntity> claimSubmission =
        claimSubmissionRepository.findFirstByReferenceIdAndIdTypeOrderByCreatedAtDesc(
            request.getClaimSubmissionId(), request.getIdType());
    if (claimSubmission.isEmpty()) {
      log.warn(
          "Could not find claim by claimSubmissionId {} from insert evidence summary document, exiting.",
          request.getClaimSubmissionId());
      return;
    }
    ClaimEntity claim = claimSubmission.get().getClaim();
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

  @Override
  public void updateRfdFlag(String benefitClaimId, boolean rfdFlag) {
    var claim = claimRepository.findByVbmsId(benefitClaimId);
    if (claim.isEmpty()) {
      log.warn("Could not find claim with id and idType, could not update RFD flag.");
      return;
    }
    ClaimEntity claimEntity = claim.get();
    claimEntity.setRfdFlag(rfdFlag);
    claimRepository.save(claimEntity);
  }

  @Override
  public void updateSufficientEvidenceFlag(AbdEvidenceWithSummary evidence, String diagnosticCode) {
    // For v1 endpoints, ClaimSubmissionId = ClaimSubmission.referenceId
    Optional<ClaimSubmissionEntity> claimSubmission =
        claimSubmissionRepository.findFirstByReferenceIdAndIdTypeOrderByCreatedAtDesc(
            evidence.getClaimSubmissionId(), evidence.getIdType());
    if (claimSubmission.isEmpty()) {
      log.warn(
          "Claim Submission not found for claim submission id = {} and id type = {} in update sufficient evidence flag",
          evidence.getClaimSubmissionId(),
          evidence.getIdType());
      return;
    }
    ClaimEntity claim = claimSubmission.get().getClaim();
    ContentionEntity contention = findContention(claim, diagnosticCode);
    if (contention == null) {
      log.warn("Could not find contention with given diagnostic code.");
      return;
    }
    Optional<AssessmentResultEntity> result =
        assessmentResultRepository.findFirstByContentionIdOrderByCreatedAtDesc(contention.getId());
    if (result.isEmpty()) {
      log.warn("Could not match assessment result to this contention id.");
      return;
    }
    if (evidence.getEvidence() == null) {
      log.warn("No evidence.");
    }
    AssessmentResultEntity assessmentResult = result.get();
    assessmentResult.setSufficientEvidenceFlag(evidence.getSufficientForFastTracking());
    assessmentResultRepository.save(assessmentResult);
  }

  private List<VeteranFlashIdEntity> createFlashIds(
      List<String> veteranFlashIds, VeteranEntity entity) {
    List<VeteranFlashIdEntity> flashIdList = new ArrayList<>();
    for (String flashId : veteranFlashIds) {
      VeteranFlashIdEntity id = new VeteranFlashIdEntity();
      id.setFlashId(flashId);
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
    // Currently ExamOrders only come from MAS
    Optional<ClaimSubmissionEntity> claimSubmission =
        claimSubmissionRepository.findFirstByReferenceIdAndIdTypeOrderByCreatedAtDesc(
            examOrder.getCollectionId(), examOrder.getIdType());
    ExamOrderEntity examOrderEntity = new ExamOrderEntity();
    examOrderEntity.setCollectionId(examOrder.getCollectionId());
    examOrderEntity.setStatus(examOrder.getStatus());
    if (claimSubmission.isEmpty()) {
      log.warn(
          "Could not find claim submission for collection id {}, will not save connection to exam order.",
          examOrder.getCollectionId());
    } else {
      examOrderEntity.setClaimSubmission(claimSubmission.get());
    }
    return examOrderRepository.save(examOrderEntity);
  }

  private ContentionEntity createContention(ClaimEntity claim, String diagnosticCode) {
    ContentionEntity contentionEntity = new ContentionEntity();
    contentionEntity.setDiagnosticCode(diagnosticCode);
    claim.addContention(contentionEntity);
    return contentionEntity;
  }

  private VeteranEntity findOrCreateVeteran(String veteranIcn) {
    VeteranEntity veteranEntity =
        veteranRepository.findByIcn(veteranIcn).orElseGet(() -> createVeteran(veteranIcn));
    Date date = new Date();
    veteranEntity.setIcnTimestamp(date);
    return veteranRepository.save(veteranEntity);
  }

  private VeteranEntity createVeteran(String veteranIcn) {
    VeteranEntity veteranEntity = new VeteranEntity();
    veteranEntity.setIcn(veteranIcn);
    return veteranRepository.save(veteranEntity);
  }
}
