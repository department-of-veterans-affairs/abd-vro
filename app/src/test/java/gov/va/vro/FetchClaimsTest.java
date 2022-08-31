package gov.va.vro;

import static org.junit.jupiter.api.Assertions.*;

import gov.va.vro.api.responses.FetchClaimsResponse;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.service.spi.services.FetchClaimsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FetchClaimsTest extends BaseIntegrationTest {

  @Autowired private FetchClaimsService fetchClaimsService;

  @Autowired private TestRestTemplate testRestTemplate;

  private <I, O> ResponseEntity<O> get(String url, I request, Class<O> responseType) {
    return exchange(url, request, HttpMethod.GET, responseType);
  }

  private <I, O> ResponseEntity<O> exchange(
      String url, I request, HttpMethod method, Class<O> responseType) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-Key", "ec4624eb-a02d-4d20-bac6-095b98a792a2");
    var httpEntity = new HttpEntity<>(request, headers);
    return testRestTemplate.exchange(url, method, httpEntity, responseType);
  }

  @Test
  void fetchClaimsPositive() {
    // Build 3 separate veterans and claims and save to DB
    // Claim 1
    var veteran = TestDataSupplier.createVeteran("ICN", "participantID");
    veteranRepository.save(veteran);
    var claim = TestDataSupplier.createClaim("1111111", "type", veteran);
    ContentionEntity contention1 = new ContentionEntity("code1");
    ContentionEntity contention2 = new ContentionEntity("code2");
    claim.addContention(contention1);
    claim.addContention(contention2);
    claimRepository.save(claim);

    // Claim 2
    var veteran2 = TestDataSupplier.createVeteran("ICN", "participantID");
    veteranRepository.save(veteran2);
    var claim2 = TestDataSupplier.createClaim("2222222", "type", veteran);
    ContentionEntity contention3 = new ContentionEntity("code3");
    ContentionEntity contention4 = new ContentionEntity("code4");
    claim2.addContention(contention3);
    claim2.addContention(contention4);
    claimRepository.save(claim2);

    // Claim3
    var veteran3 = TestDataSupplier.createVeteran("ICN", "participantID");
    veteranRepository.save(veteran3);
    var claim3 = TestDataSupplier.createClaim("3333333", "type", veteran);
    ContentionEntity contention5 = new ContentionEntity("code5");
    ContentionEntity contention6 = new ContentionEntity("code6");
    claim3.addContention(contention5);
    claim3.addContention(contention6);
    claimRepository.save(claim3);

    // Run the fetch-claims endpoint to fetch all claims in DB
    // Save full HTTP response in responseEntity
    // Save FetchClaimsResponse object in response
    var test = claimRepository.findAll();
    ResponseEntity<FetchClaimsResponse> responseEntity =
        get("/v1/fetch-claims", null, FetchClaimsResponse.class);
    FetchClaimsResponse response = responseEntity.getBody();

    // Verify that the status code os 'OK' and the list size is 3
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(response);
    assertEquals(3, response.getClaims().size());

    // Verify content in each claim is correct
    // Verify Claim1
    assertEquals(claim.getClaimSubmissionId(), response.getClaims().get(0).getClaimSubmissionId());
    assertEquals(claim.getVeteran().getIcn(), response.getClaims().get(0).getVeteranIcn());
    assertEquals(2, response.getClaims().get(0).getContentions().size());
    assertTrue(response.getClaims().get(0).getContentions().contains("code1"));
    assertTrue(response.getClaims().get(0).getContentions().contains("code2"));

    // Verify Claim2
    assertEquals(claim2.getClaimSubmissionId(), response.getClaims().get(1).getClaimSubmissionId());
    assertEquals(claim2.getVeteran().getIcn(), response.getClaims().get(1).getVeteranIcn());
    assertEquals(2, response.getClaims().get(1).getContentions().size());
    assertTrue(response.getClaims().get(1).getContentions().contains("code3"));
    assertTrue(response.getClaims().get(1).getContentions().contains("code4"));

    // Verify Claim3
    assertEquals(claim3.getClaimSubmissionId(), response.getClaims().get(2).getClaimSubmissionId());
    assertEquals(claim3.getVeteran().getIcn(), response.getClaims().get(2).getVeteranIcn());
    assertEquals(2, response.getClaims().get(2).getContentions().size());
    assertTrue(response.getClaims().get(2).getContentions().contains("code5"));
    assertTrue(response.getClaims().get(2).getContentions().contains("code6"));
  }
}
