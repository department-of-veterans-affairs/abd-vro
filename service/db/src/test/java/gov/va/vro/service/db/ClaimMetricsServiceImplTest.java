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
  void testClaimInfoForVeteran() throws IOException {
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

  @Test
  void testClaimInfoWithPagination() throws IOException {
    // Creates claim with contention, assessment results, and evidence summary documents.
    ClaimEntity claim = saveTestData("1234");
    //    ClaimEntity claim2 = saveTestData("2345");
    //    ClaimEntity claim3 = saveTestData("3456");
    //    ClaimEntity claim4 = saveTestData("4567");
    //    ClaimEntity claim5 = saveTestData("5678");

    // Run claim-info endpoint
    List<ClaimInfoData> claimInfoList = claimMetricsService.claimInfoWithPagination(0, 5);
    // test each result
    ClaimInfoData result1 = claimInfoList.get(0);
    assertEquals(result1.getClaimSubmissionId(), claim.getClaimSubmissionId());
    assertEquals(result1.getContentionsCount(), claim.getContentions().size());
    assertEquals(result1.getVeteranIcn(), claim.getVeteran().getIcn());
    assertEquals(
        result1.getAssessmentResultsCount(),
        claim.getContentions().get(0).getAssessmentResults().size());
    assertEquals(
        result1.getEvidenceSummary(),
        claim.getContentions().get(0).getAssessmentResults().get(0).getEvidenceCountSummary());
    assertEquals(result1.getEvidenceSummaryDocumentsCount(), 1);

    //    ClaimInfoData result2 = claimInfoList.get(1);
    //    assertEquals(result2.getClaimSubmissionId(), claim2.getClaimSubmissionId());
    //    assertEquals(result2.getContentionsCount(), claim2.getContentions().size());
    //    assertEquals(result2.getVeteranIcn(), claim2.getVeteran().getIcn());
    //    assertEquals(
    //        result2.getAssessmentResultsCount(),
    //        claim2.getContentions().get(1).getAssessmentResults().size());
    //    assertEquals(
    //        result2.getEvidenceSummary(),
    //
    // claim2.getContentions().get(1).getAssessmentResults().get(1).getEvidenceCountSummary());
    //    assertEquals(result2.getEvidenceSummaryDocumentsCount(), 1);
    //
    //    ClaimInfoData result3 = claimInfoList.get(2);
    //    assertEquals(result3.getClaimSubmissionId(), claim3.getClaimSubmissionId());
    //    assertEquals(result3.getContentionsCount(), claim3.getContentions().size());
    //    assertEquals(result3.getVeteranIcn(), claim3.getVeteran().getIcn());
    //    assertEquals(
    //        result3.getAssessmentResultsCount(),
    //        claim3.getContentions().get(2).getAssessmentResults().size());
    //    assertEquals(
    //        result3.getEvidenceSummary(),
    //
    // claim3.getContentions().get(2).getAssessmentResults().get(2).getEvidenceCountSummary());
    //    assertEquals(result3.getEvidenceSummaryDocumentsCount(), 1);
    //
    //    ClaimInfoData result4 = claimInfoList.get(3);
    //    assertEquals(result4.getClaimSubmissionId(), claim4.getClaimSubmissionId());
    //    assertEquals(result4.getContentionsCount(), claim4.getContentions().size());
    //    assertEquals(result4.getVeteranIcn(), claim4.getVeteran().getIcn());
    //    assertEquals(
    //        result4.getAssessmentResultsCount(),
    //        claim4.getContentions().get(3).getAssessmentResults().size());
    //    assertEquals(
    //        result4.getEvidenceSummary(),
    //
    // claim4.getContentions().get(3).getAssessmentResults().get(3).getEvidenceCountSummary());
    //    assertEquals(result4.getEvidenceSummaryDocumentsCount(), 1);
    //
    //    ClaimInfoData result5 = claimInfoList.get(4);
    //    assertEquals(result5.getClaimSubmissionId(), claim5.getClaimSubmissionId());
    //    assertEquals(result5.getContentionsCount(), claim5.getContentions().size());
    //    assertEquals(result5.getVeteranIcn(), claim5.getVeteran().getIcn());
    //    assertEquals(
    //        result5.getAssessmentResultsCount(),
    //        claim5.getContentions().get(4).getAssessmentResults().size());
    //    assertEquals(
    //        result5.getEvidenceSummary(),
    //
    // claim5.getContentions().get(4).getAssessmentResults().get(4).getEvidenceCountSummary());
    //    assertEquals(result5.getEvidenceSummaryDocumentsCount(), 1);
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
