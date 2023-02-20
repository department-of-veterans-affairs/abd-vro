package gov.va.vro.service.db.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tngtech.archunit.thirdparty.com.google.common.collect.ImmutableMap;
import gov.va.vro.model.claimmetrics.AssessmentInfo;
import gov.va.vro.model.claimmetrics.ContentionInfo;
import gov.va.vro.model.claimmetrics.DocumentInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ClaimSubmissionEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import gov.va.vro.service.db.TestConfig;
import gov.va.vro.service.spi.model.Claim;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ActiveProfiles("test")
@EnableJpaAuditing
public class ClaimInfoResponseMapperTest {
  @Autowired ClaimInfoResponseMapper mapper;

  @Test
  void testMain() {
    final String icn = "icn_test";
    VeteranEntity veteranEntity = new VeteranEntity();
    veteranEntity.setIcn(icn);

    final String diagnosticCode = "7101";
    ContentionEntity contentionEntity = new ContentionEntity();
    contentionEntity.setDiagnosticCode(diagnosticCode);

    AssessmentResultEntity assessmentResultEntity = new AssessmentResultEntity();
    Map<String, String> areEvidenceInfo = ImmutableMap.of("k1", "v1", "k2", "v2");
    assessmentResultEntity.setEvidenceCountSummary(areEvidenceInfo);
    contentionEntity.addAssessmentResult(assessmentResultEntity);

    EvidenceSummaryDocumentEntity esdEntity = new EvidenceSummaryDocumentEntity();
    final String documentName = "document_name_test";
    esdEntity.setDocumentName(documentName);
    Map<String, String> esdEvidenceInfo = ImmutableMap.of("k3", "v3", "k4", "v4");
    esdEntity.setEvidenceCount(esdEvidenceInfo);
    contentionEntity.addEvidenceSummaryDocument(esdEntity);

    final String claimSubmissionId = "cs_id_test";
    ClaimSubmissionEntity claimSubmissionEntity = new ClaimSubmissionEntity();
    claimSubmissionEntity.setReferenceId(claimSubmissionId);
    claimSubmissionEntity.setIdType(Claim.V1_ID_TYPE);

    ClaimEntity claimEntity = new ClaimEntity();
    claimEntity.setVeteran(veteranEntity);
    claimEntity.addContention(contentionEntity);
    claimEntity.addClaimSubmission(claimSubmissionEntity);

    ClaimInfoResponse response = mapper.toClaimInfoResponse(claimEntity);

    assertEquals(claimSubmissionId, response.getClaimSubmissionId());
    assertEquals(icn, response.getVeteranIcn());

    List<ContentionInfo> contentions = response.getContentions();
    assertNotNull(contentions);
    assertEquals(1, contentions.size());
    ContentionInfo contentionInfo = contentions.get(0);
    assertEquals(diagnosticCode, contentionInfo.getDiagnosticCode());

    List<AssessmentInfo> assessments = contentionInfo.getAssessments();
    assertNotNull(assessments);
    assertEquals(1, assessments.size());
    AssessmentInfo assessmentInfo = assessments.get(0);
    assertEquals(areEvidenceInfo, assessmentInfo.getEvidenceInfo());

    List<DocumentInfo> documents = contentionInfo.getDocuments();
    assertNotNull(documents);
    assertEquals(1, documents.size());
    DocumentInfo documentInfo = documents.get(0);
    assertEquals(documentName, documentInfo.getDocumentName());
    assertEquals(esdEvidenceInfo, documentInfo.getEvidenceInfo());
  }
}
