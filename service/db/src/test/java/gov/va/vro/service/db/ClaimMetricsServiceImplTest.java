package gov.va.vro.service.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.ClaimInfoData;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class ClaimMetricsServiceImplTest {

  @Autowired private ClaimMetricsServiceImpl claimMetricsService;

  @Autowired private SaveToDbServiceImpl saveToDbService;

  @Autowired private ClaimRepository claimRepository;

  @Value("classpath:test-data/evidence-summary-document-data.json")
  private Resource esdData;

  @Test
  void testClaimInfo() throws IOException {
    // Creates claim with contention, assessment results, and evidence summary documents.
    ClaimEntity claim = saveTestData("1234");
    // Run claim-info endpoint and test
    ClaimInfoData claimInfo = claimMetricsService.claimInfoForClaimId("1234");
    assertEquals(claimInfo.getClaimSubmissionId(), claim.getClaimSubmissionId());
    assertEquals(claimInfo.getContentionsCount(), claim.getContentions().size());
    assertEquals(claimInfo.getVeteranIcn(), claim.getVeteran().getIcn());
    assertEquals(
        claimInfo.getAssessmentResultsCount(),
        claim.getContentions().get(0).getAssessmentResults().size());
    assertEquals(
        claimInfo.getEvidenceSummary(),
        claim.getContentions().get(0).getAssessmentResults().get(0).getEvidenceCountSummary());
    assertEquals(claimInfo.getEvidenceSummaryDocumentsCount(), 1);
  }

  @Test
  void testClaimInfoForAll() throws IOException {
    // Creates claim with contention, assessment results, and evidence summary documents.
    ClaimEntity claim = saveTestData("1234");
    // Run claim-info endpoint and test
    List<ClaimInfoData> claimInfoList = claimMetricsService.claimInfoForVeteran("v1");
    ClaimInfoData result = claimInfoList.get(0);
    assertEquals(result.getClaimSubmissionId(), claim.getClaimSubmissionId());
    assertEquals(result.getContentionsCount(), claim.getContentions().size());
    assertEquals(result.getVeteranIcn(), claim.getVeteran().getIcn());
    assertEquals(
        result.getAssessmentResultsCount(),
        claim.getContentions().get(0).getAssessmentResults().size());
    assertEquals(
        result.getEvidenceSummary(),
        claim.getContentions().get(0).getAssessmentResults().get(0).getEvidenceCountSummary());
    assertEquals(result.getEvidenceSummaryDocumentsCount(), 1);
  }

  ClaimEntity saveTestData(String claimSubmissionId) throws IOException {
    // Create claim and save
    Claim claim = new Claim();
    claim.setClaimSubmissionId(claimSubmissionId);
    claim.setVeteranIcn("v1");
    claim.setDiagnosticCode("7101");
    saveToDbService.insertClaim(claim);
    // Add a contention and an assessment result
    ClaimEntity claimBeforeAssessment =
        claimRepository.findByClaimSubmissionId(claimSubmissionId).orElseThrow();
    ContentionEntity contention = new ContentionEntity("7101");
    claimBeforeAssessment.addContention(contention);
    Map<String, Object> evidenceMap = new HashMap<>();
    evidenceMap.put("medicationsCount", "10");
    AbdEvidenceWithSummary evidence = new AbdEvidenceWithSummary();
    evidence.setEvidenceSummary(evidenceMap);
    saveToDbService.insertAssessmentResult(
        claimBeforeAssessment.getId(), evidence, contention.getDiagnosticCode());
    // Add an evidence summary document
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
    return claimRepository.findByClaimSubmissionId(claimSubmissionId).orElseThrow();
  }
}
