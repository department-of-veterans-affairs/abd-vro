package gov.va.vro.service.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity;
import gov.va.vro.persistence.model.VeteranEntity;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        claimRepository.findByVbmsId(claim.getClaimSubmissionId()).orElseThrow();
    assertEquals(claim.getClaimSubmissionId(), claimEntity.getVbmsId());
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
    saveToDbService.insertClaim(claim);
    ClaimEntity claimBeforeAssessment = claimRepository.findByVbmsId("1234").orElseThrow();
    Map<String, Object> evidenceMap = new HashMap<>();
    evidenceMap.put("medicationsCount", "10");
    AbdEvidenceWithSummary evidence = new AbdEvidenceWithSummary();
    evidence.setEvidenceSummary(evidenceMap);
    saveToDbService.insertAssessmentResult(claimBeforeAssessment.getId(), evidence, "7101");
    ClaimEntity result = claimRepository.findByVbmsId("1234").orElseThrow();
    assertNotNull(result);
    assertNotNull(result.getContentions().get(0).getAssessmentResults().get(0));
    AssessmentResultEntity assessmentResult =
        result.getContentions().get(0).getAssessmentResults().get(0);
    assertEquals(assessmentResult.getEvidenceCountSummary(), evidenceMap);

    long c = assessmentResultRepository.count();
    assertEquals(1, c);
  }

  @Test
  void persistEvidenceSummaryDocument() throws Exception {
    // Save claim
    Claim claim = new Claim();
    claim.setClaimSubmissionId("1234");
    claim.setVeteranIcn("v1");
    claim.setDiagnosticCode("7101");
    saveToDbService.insertClaim(claim);
    // Build evidence
    InputStream stream = esdData.getInputStream();
    String inputAsString = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    ObjectMapper mapper = new ObjectMapper();
    GeneratePdfPayload input = mapper.readValue(inputAsString, GeneratePdfPayload.class);
    String diagnosis = "Hypertension";
    String documentName = GeneratePdfPayload.createPdfFilename(diagnosis);
    // Save evidence summary document.
    saveToDbService.insertEvidenceSummaryDocument(input, documentName);
    ClaimEntity result = claimRepository.findByVbmsId("1234").orElseThrow();
    // Verify evidence is correct
    assertNotNull(result);
    EvidenceSummaryDocumentEntity esd =
        result.getContentions().get(0).getEvidenceSummaryDocuments().get(0);
    assertNotNull(esd);
    assertEquals(esd.getDocumentName(), documentName);
    assertEquals(esd.getEvidenceCount().size(), 2);
  }

  @Test
  void persistFlashIds() {
    VeteranEntity veteran = new VeteranEntity();
    veteran.setIcn("X");
    veteran.setParticipantId("Y");
    veteranRepository.save(veteran);
    List<String> flashIds = new ArrayList<>();
    flashIds.add("123");
    flashIds.add("456");
    saveToDbService.insertFlashIds(flashIds, veteran.getIcn());
    VeteranEntity veteranWithFlashIds = veteranRepository.findByIcn(veteran.getIcn()).orElseThrow();
    assertEquals(
        veteranWithFlashIds.getFlashIds().get(0).getFlashId(), Integer.valueOf(flashIds.get(0)));
    assertEquals(
        veteranWithFlashIds.getFlashIds().get(1).getFlashId(), Integer.valueOf(flashIds.get(1)));
    assertEquals(veteranWithFlashIds.getFlashIds().get(0).getVeteran().getIcn(), veteran.getIcn());
    assertEquals(veteranWithFlashIds.getFlashIds().get(1).getVeteran().getIcn(), veteran.getIcn());
  }

  @Test
  void multipleRequests() {
    Claim claim1 =
        Claim.builder()
            .claimSubmissionId("1234")
            .collectionId("111")
            .veteranIcn("v1")
            .diagnosticCode("7101")
            .build();
    saveToDbService.insertClaim(claim1);
    ClaimEntity claimEntity1 = claimRepository.findByVbmsId("1234").orElseThrow();
    assertEquals(1, claimEntity1.getContentions().size());
    ContentionEntity contentionEntity = claimEntity1.getContentions().get(0);
    assertEquals(claim1.getDiagnosticCode(), contentionEntity.getDiagnosticCode());

    Claim claim2 =
        Claim.builder()
            .claimSubmissionId("1234")
            .collectionId("111")
            .veteranIcn("v1")
            .diagnosticCode("8181")
            .build();
    saveToDbService.insertClaim(claim2);
    ClaimEntity claimEntity2 = claimRepository.findByVbmsId("1234").orElseThrow();
    assertEquals(2, claimEntity2.getContentions().size());
  }
}
