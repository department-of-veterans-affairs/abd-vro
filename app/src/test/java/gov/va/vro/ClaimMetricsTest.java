package gov.va.vro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.api.responses.ClaimMetricsResponse;
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
    // Veteran and claim 1
    var veteran1 = TestDataSupplier.createVeteran("112233", "ae17ne34z");
    veteranRepository.save(veteran1);
    var claim1 = TestDataSupplier.createClaim("123456789", "IdType", veteran1);
    claimRepository.save(claim1);
    // Veteran and claim 2
    var veteran2 = TestDataSupplier.createVeteran("112233", "ae17ne34z");
    veteranRepository.save(veteran2);
    var claim2 = TestDataSupplier.createClaim("123456789", "IdType", veteran2);
    claimRepository.save(claim2);

    Long total = claimRepository.count();

    ResponseEntity<ClaimMetricsResponse> responseEntity =
        get("/v1/claim-metrics", null, ClaimMetricsResponse.class);
    ClaimMetricsResponse response = responseEntity.getBody();
    assertNotNull(response);
    assertEquals(response.getClaims(), total);
  }
}
