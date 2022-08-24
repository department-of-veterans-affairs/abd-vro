package gov.va.vro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.api.responses.FetchClaimsResponse;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.service.spi.services.fetchclaims.FetchClaimsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FetchClaimsTest extends BaseIntegrationTest {

  @Autowired private FetchClaimsService fetchClaimsService;

  @Autowired private TestRestTemplate testRestTemplate;

  @Test
  void fetchClaimsPositive() {
    //Build 3 separate veterans and claims and save to DB
    // Claim 1
    var veteran = TestDataSupplier.createVeteran("ICN", "participantID");
    veteranRepository.save(veteran);
    var claim = TestDataSupplier.createClaim("1111111", "type", veteran);
    ContentionEntity contention1 = new ContentionEntity("code1");
    ContentionEntity contention2 = new ContentionEntity("code2");
    List<ContentionEntity> contentionEntities = new ArrayList<>();
    contentionEntities.add(contention1);
    contentionEntities.add(contention2);
    claim.setContentions(contentionEntities);
    claimRepository.save(claim);

    // Claim 2
    var veteran2 = TestDataSupplier.createVeteran("ICN", "participantID");
    veteranRepository.save(veteran2);
    var claim2 = TestDataSupplier.createClaim("2222222", "type", veteran);
    ContentionEntity contention3 = new ContentionEntity("code3");
    ContentionEntity contention4 = new ContentionEntity("code4");
    List<ContentionEntity> contentionEntities2 = new ArrayList<>();
    contentionEntities2.add(contention3);
    contentionEntities2.add(contention4);
    claim2.setContentions(contentionEntities2);
    claimRepository.save(claim2);

    // Claim3
    var veteran3 = TestDataSupplier.createVeteran("ICN", "participantID");
    veteranRepository.save(veteran3);
    var claim3 = TestDataSupplier.createClaim("3333333", "type", veteran);
    ContentionEntity contention5 = new ContentionEntity("code5");
    ContentionEntity contention6 = new ContentionEntity("code6");
    List<ContentionEntity> contentionEntities3 = new ArrayList<>();
    contentionEntities3.add(contention5);
    contentionEntities3.add(contention6);
    claim3.setContentions(contentionEntities3);
    claimRepository.save(claim3);

    //Run the fetch-claims endpoint to fetch all claims in DB
    //Save full HTTP response in responseEntity
    //Save FetchClaimsResponse object in response
    ResponseEntity<FetchClaimsResponse> responseEntity =
        testRestTemplate.getForEntity("v1/fetch-claims", FetchClaimsResponse.class);
    FetchClaimsResponse response = responseEntity.getBody();

    //Verify that the status code os 'OK' and the list size is 3
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assert response != null;
    assertEquals(3, response.getClaims().size());

    //Verify content in each claim is correct
    //Verify Claim1
    assertEquals(claim.getClaimSubmissionId(), response.getClaims().get(0).getClaimSubmissionId());
    assertEquals(claim.getVeteran().getIcn(), response.getClaims().get(0).getVeteranIcn());
    assertEquals(2, response.getClaims().get(0).getContentions().size());
    assertEquals(claim.getContentions().get(0).getDiagnosticCode(), response.getClaims().get(0).getContentions().get(0));
    assertEquals(claim.getContentions().get(1).getDiagnosticCode(), response.getClaims().get(0).getContentions().get(1));

    //Verify Claim2
    assertEquals(claim2.getClaimSubmissionId(), response.getClaims().get(1).getClaimSubmissionId());
    assertEquals(claim2.getVeteran().getIcn(), response.getClaims().get(1).getVeteranIcn());
    assertEquals(2, response.getClaims().get(1).getContentions().size());
    assertEquals(claim2.getContentions().get(0).getDiagnosticCode(), response.getClaims().get(1).getContentions().get(0));
    assertEquals(claim2.getContentions().get(1).getDiagnosticCode(), response.getClaims().get(1).getContentions().get(1));

    //Verify Claim3
    assertEquals(claim3.getClaimSubmissionId(), response.getClaims().get(2).getClaimSubmissionId());
    assertEquals(claim3.getVeteran().getIcn(), response.getClaims().get(2).getVeteranIcn());
    assertEquals(2, response.getClaims().get(2).getContentions().size());
    assertEquals(claim3.getContentions().get(0).getDiagnosticCode(), response.getClaims().get(2).getContentions().get(0));
    assertEquals(claim3.getContentions().get(1).getDiagnosticCode(), response.getClaims().get(2).getContentions().get(1));

  }

  //Need a test for when the service fails and error messages need to be displayed
}
