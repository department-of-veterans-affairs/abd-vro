package gov.va.vro.service.db.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.model.VeteranInfo;
import gov.va.vro.model.claimmetrics.AssessmentInfo;
import gov.va.vro.model.claimmetrics.ContentionInfo;
import gov.va.vro.model.claimmetrics.DocumentInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ClaimSubmissionEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.ClaimSubmissionRepository;
import gov.va.vro.service.spi.db.SaveToDbService;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class contains the data, database insert methods, and testing assertions for typical
 * workflows that populate the database. It is being used as a utility class for Save to Db and
 * Claim Metrics functionality.
 */
@Getter
public class ClaimMetricsTestCase {
  private static final AtomicInteger counter = new AtomicInteger(0);

  // private ServiceBundle serviceBundle;

  private String claimSubmissionId;

  private String icn;

  private AbdEvidenceCase evidenceCase;

  private String documentName;

  private VeteranInfo veteranInfo;

  private GeneratePdfPayload getPdfPayload(AbdEvidence evidence) {
    GeneratePdfPayload result = new GeneratePdfPayload();

    result.setEvidence(evidence);
    result.setDiagnosticCode("7101");
    result.setClaimSubmissionId(claimSubmissionId);
    result.setVeteranInfo(veteranInfo);

    return result;
  }

  /**
   * This populates the database based on the simulation of generating medical assessment and
   * evidence summary pdf.
   */
  public void populate(
      SaveToDbService service, ClaimSubmissionRepository csRepo) {
    Claim claim = new Claim();
    claim.setCollectionId(claimSubmissionId);
    claim.setVeteranIcn(icn);
    claim.setDiagnosticCode("7101");
    service.insertClaim(claim);

    ClaimEntity claimEntity;

    // v1 Some calls send us claimSubmissionId which is the same as reference_id on the
    // claim_submission table (which is collectionId as well)
    ClaimSubmissionEntity csEntity =
        csRepo.findFirstByReferenceIdAndIdTypeOrderByCreatedAtDesc(claimSubmissionId, claim.getIdType()).orElseThrow();
    claimEntity = csEntity.getClaim();

    Set<ClaimSubmissionEntity> submissions = claimEntity.getClaimSubmissions();
    assertEquals(1, submissions.size());
    ClaimSubmissionEntity submissionEntity = submissions.iterator().next();
    assertEquals(claimSubmissionId, submissionEntity.getReferenceId());
    List<ContentionEntity> contentions = claimEntity.getContentions();
    assertEquals(1, contentions.size());

    UUID claimEntityId = claimEntity.getId();
    AbdEvidenceWithSummary evidence = evidenceCase.getEvidenceWithSummary(claimSubmissionId);
    service.insertAssessmentResult(claimEntityId, evidence, "7101");

    GeneratePdfPayload gpp = getPdfPayload(evidence.getEvidence());
    service.insertEvidenceSummaryDocument(gpp, documentName);
  }

  private static VeteranInfo getVeteranInfo(int index) {
    VeteranInfo veteranInfo = new VeteranInfo();

    veteranInfo.setLast("last_" + index);
    veteranInfo.setFirst("first_" + index);

    int month = (index % 12) + 1;
    int year = 1980 + (index % 20);
    int day = index % 28;
    veteranInfo.setBirthdate(month + "/" + day + "/" + year);

    return veteranInfo;
  }

  /**
   * Constructs a new claim test case for the same veteran.
   *
   * @return newed TestSetup
   */
  public ClaimMetricsTestCase newCaseForSameVeteran() {
    ClaimMetricsTestCase result = new ClaimMetricsTestCase();

    int counterValue = counter.getAndIncrement();

    result.claimSubmissionId = "claim_id_" + counterValue;
    result.evidenceCase = AbdEvidenceCase.getInstance();
    result.documentName = "document_" + counterValue;

    result.icn = icn;
    result.veteranInfo = getVeteranInfo(counterValue);

    return result;
  }

  /**
   * Verifies if the actual claim information is as expected.
   *
   * @param claimInfo
   */
  public void verifyClaimInfoResponse(ClaimInfoResponse claimInfo) {
    assertEquals(claimSubmissionId, claimInfo.getClaimSubmissionId());
    assertEquals(icn, claimInfo.getVeteranIcn());

    List<ContentionInfo> contentions = claimInfo.getContentions();
    assertNotNull(contentions);
    assertEquals(1, contentions.size());
    ContentionInfo contention = contentions.get(0);

    assertEquals("7101", contention.getDiagnosticCode());

    List<AssessmentInfo> assessments = contention.getAssessments();
    assertNotNull(assessments);
    assertEquals(1, assessments.size());
    AssessmentInfo assessment = assessments.get(0);
    evidenceCase.verifyEvidenceSummary(assessment.getEvidenceInfo());

    List<DocumentInfo> documents = contention.getDocuments();
    assertNotNull(documents);
    assertEquals(1, documents.size());
    DocumentInfo document = documents.get(0);
    assertEquals(documentName, document.getDocumentName());
    evidenceCase.verifyEvidenceSummary(document.getEvidenceInfo());
  }

  /**
   * Constructs a new test set that defaults data based on static counter.
   *
   * @return newed TestSetup
   */
  public static ClaimMetricsTestCase getInstance() {
    ClaimMetricsTestCase result = new ClaimMetricsTestCase();

    int counterValue = counter.getAndIncrement();

    result.claimSubmissionId = "claim_id_" + counterValue;
    result.icn = "icn_" + counterValue;
    result.evidenceCase = AbdEvidenceCase.getInstance();
    result.documentName = "document_" + counterValue;
    result.veteranInfo = getVeteranInfo(counterValue);

    return result;
  }
}
