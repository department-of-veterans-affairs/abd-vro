package gov.va.vro.service.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity;
import gov.va.vro.persistence.repository.AssessmentResultRepository;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ActiveProfiles("test")
@EnableJpaAuditing
class SaveToDbServiceImplTest {

  @Autowired private SaveToDbServiceImpl saveToDbService;

  @Autowired private VeteranRepository veteranRepository;

  @Autowired private ClaimRepository claimRepository;

  @Autowired private AssessmentResultRepository assessmentResultRepository;

  @Value("classpath:test-data/evidence-summary-document-data.json")
  private Resource esdData;

  @Test
  void persistClaim() {
    Claim claim = new Claim();
    claim.setClaimSubmissionId("claim1");
    claim.setVeteranIcn("v1");
    claim.setDiagnosticCode("1234");
    var result = saveToDbService.insertClaim(claim);
    assertNotNull(result.getRecordId());
    assertEquals(claim.getClaimSubmissionId(), result.getClaimSubmissionId());
    assertEquals(claim.getIdType(), result.getIdType());
    assertEquals(claim.getDiagnosticCode(), result.getDiagnosticCode());
    assertEquals(claim.getVeteranIcn(), result.getVeteranIcn());
    assertEquals(claim.getIncomingStatus(), result.getIncomingStatus());

    assertEquals(1, veteranRepository.findAll().size());
    assertEquals(1, claimRepository.findAll().size());
    ClaimEntity claimEntity =
        claimRepository
            .findByClaimSubmissionIdAndIdType(claim.getClaimSubmissionId(), claim.getIdType())
            .orElseThrow();
    assertEquals(claim.getClaimSubmissionId(), claimEntity.getClaimSubmissionId());
    assertEquals("va.gov-Form526Submission", claimEntity.getIdType());
    assertEquals("submission", claimEntity.getIncomingStatus());
    assertEquals(claim.getVeteranIcn(), claimEntity.getVeteran().getIcn());
    assertEquals(1, claimEntity.getContentions().size());
    ContentionEntity contentionEntity = claimEntity.getContentions().get(0);
    assertEquals(claim.getDiagnosticCode(), contentionEntity.getDiagnosticCode());
  }

  @Test
  void persistAssessmentResult() throws Exception {
    // Save claim
    Claim claim = new Claim();
    claim.setClaimSubmissionId("1234");
    claim.setVeteranIcn("v1");
    claim.setDiagnosticCode("7101");
    var result = saveToDbService.insertClaim(claim);
    ClaimEntity claimBeforeAssessment =
        claimRepository
            .findByClaimSubmissionIdAndIdType(claim.getClaimSubmissionId(), claim.getIdType())
            .orElseThrow();
    // ContentionEntity contention = new ContentionEntity("7101");
    // claimBeforeAssessment.addContention(contention);
    Map<String, Object> evidenceMap = new HashMap<>();
    evidenceMap.put("medicationsCount", "10");
    AbdEvidenceWithSummary evidence = new AbdEvidenceWithSummary();
    evidence.setEvidenceSummary(evidenceMap);
    saveToDbService.insertAssessmentResult(
        claimBeforeAssessment.getClaimSubmissionId(),
        claimBeforeAssessment.getIdType(),
        evidence,
        "7101");
    ClaimEntity result2 = claimRepository.findByClaimSubmissionId("1234").orElseThrow();
    assertNotNull(result2);
    assertNotNull(result2.getContentions().get(0).getAssessmentResults().get(0));
    AssessmentResultEntity assessmentResult =
        result2.getContentions().get(0).getAssessmentResults().get(0);
    assertEquals(assessmentResult.getEvidenceCountSummary(), evidenceMap);

    long c = assessmentResultRepository.count();
    assertEquals(1, c);
  }

  @Test
  void persistEvidenceSummaryDocument() throws Exception {
    // Save claim
    Claim claim = new Claim();
    claim.setClaimSubmissionId("1234");
    claim.setIdType("va.gov-Form526Submission");
    claim.setVeteranIcn("v1");
    claim.setDiagnosticCode("7101");
    var result = saveToDbService.insertClaim(claim);
    // Build evidence
    InputStream stream = esdData.getInputStream();
    String inputAsString = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    ObjectMapper mapper = new ObjectMapper();
    GeneratePdfPayload input = mapper.readValue(inputAsString, GeneratePdfPayload.class);
    String timestamp = String.format("%1$tY%1$tm%1$td", new Date());
    String diagnosis = "Hypertension";
    String documentName =
        String.format("VAMC_%s_Rapid_Decision_Evidence--%s.pdf", diagnosis, timestamp);
    // Save evidence summary document.
    saveToDbService.insertEvidenceSummaryDocument(input, documentName);
    ClaimEntity result2 =
        claimRepository
            .findByClaimSubmissionIdAndIdType(result.getClaimSubmissionId(), result.getIdType())
            .orElseThrow();
    // Verify evidence is correct
    assertNotNull(result2);
    EvidenceSummaryDocumentEntity esd =
        result2.getContentions().get(0).getEvidenceSummaryDocuments().get(0);
    assertNotNull(esd);
    assertEquals(esd.getDocumentName(), documentName);
    assertEquals(esd.getEvidenceCount().size(), 2);
  }
}
