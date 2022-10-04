package gov.va.vro;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.api.model.mas.ClaimDetail;
import gov.va.vro.api.model.mas.ClaimDetailConditions;
import gov.va.vro.api.model.mas.VeteranIdentifiers;
import gov.va.vro.api.requests.MasClaimDetailsRequest;
import gov.va.vro.api.requests.MasClaimResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MasControllerTest extends BaseControllerTest {

  @Test
  void notifyVROAutomatedClaimDetails_invalidRequest() {
    MasClaimDetailsRequest request =
        MasClaimDetailsRequest.builder().dob("2002-12-12").collectionsid("123").build();

    var responseEntity =
        post("/v1/notifyVROAutomatedClaimDetails", request, MasClaimResponse.class);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }

  @Test
  void notifyVROAutomatedClaimDetails_validRequest() {
    VeteranIdentifiers veteranIdentifiers = new VeteranIdentifiers();
    veteranIdentifiers.setEdipn("X");
    veteranIdentifiers.setParticipantid("X");
    veteranIdentifiers.setIcn("X");
    veteranIdentifiers.setSsn("X");
    veteranIdentifiers.setVeteranfileid("X");
    ClaimDetailConditions conditions = new ClaimDetailConditions();
    conditions.setDiagnosticcode("1233");
    ClaimDetail claimDetail = new ClaimDetail();
    claimDetail.setConditions(conditions);

    MasClaimDetailsRequest request =
        MasClaimDetailsRequest.builder()
            .dob("2002-12-12")
            .collectionsid("123")
            .firstname("Rick")
            .lastname("Smith")
            .veteranidentifiers(veteranIdentifiers)
            .claimdetail(claimDetail)
            .build();

    var responseEntity =
        post("/v1/notifyVROAutomatedClaimDetails", request, MasClaimResponse.class);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }
}
