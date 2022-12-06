package gov.va.vro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.api.model.ClaimInfo;
import gov.va.vro.api.responses.ClaimMetricsResponse;
import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.service.spi.services.ClaimMetricsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ClaimMetricsTest extends BaseIntegrationTest {

  @Autowired private TestRestTemplate testRestTemplate;

  @Autowired private ClaimMetricsService claimMetricsService;

  private <I, O> ResponseEntity<O> get(String url, I request, Class<O> responseType) {
    return exchange(url, request, HttpMethod.GET, responseType);
  }

  private <I, O> ResponseEntity<O> exchange(
      String url, I request, HttpMethod method, Class<O> responseType) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-Key", "test-key-01");
    var httpEntity = new HttpEntity<>(request, headers);
    return testRestTemplate.exchange(url, method, httpEntity, responseType);
  }

  @Test
  void verifyClaimMetrics() {
    // Veteran and claim
    var veteran = TestDataSupplier.createVeteran("112233", "ae17ne34z");
    veteranRepository.save(veteran);
    var claim = TestDataSupplier.createClaim("1234", "IdType", veteran);
    ContentionEntity contention = new ContentionEntity("7101");
    AssessmentResultEntity assessmentResult = new AssessmentResultEntity();
    Map<String, String> evidence = new HashMap<>();
    evidence.put("medicationsCount", "1");
    assessmentResult.setEvidenceCountSummary(evidence);
    contention.addAssessmentResult(assessmentResult);
    claim.addContention(contention);
    claimRepository.save(claim);

    ResponseEntity<ClaimMetricsResponse> responseEntity =
        get("/v1/claim-info/1234", null, ClaimMetricsResponse.class);
    ClaimMetricsResponse response = responseEntity.getBody();
    assertNotNull(response.getClaims());
    ClaimInfo info = response.getClaims().get(0);
    assertEquals(info.getClaimSubmissionId(), "1234");
    assertEquals(info.getVeteranIcn(), veteran.getIcn());
    assertEquals(info.getAssessmentResultsCount(), 1);
    assertEquals(info.getEvidenceSummary(), evidence);
  }
}
