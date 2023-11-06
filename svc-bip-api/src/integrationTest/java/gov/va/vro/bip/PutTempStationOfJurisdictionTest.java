package gov.va.vro.bip;

import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionRequest;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PutTempStationOfJurisdictionTest extends BaseIntegrationTest {

  @Test
  void testPutTempStationOfJurisdiction_200() {
    PutTempStationOfJurisdictionRequest request =
        PutTempStationOfJurisdictionRequest.builder()
            .claimId(CLAIM_ID_200)
            .tempStationOfJurisdiction("398")
            .build();
    PutTempStationOfJurisdictionResponse response =
        sendAndReceive(putTempStationOfJurisdictionQueue, request);
    assertBaseResponseIs200(response);
  }

  @Test
  void testPutTempStationOfJurisdiction_404() {
    PutTempStationOfJurisdictionRequest request =
        PutTempStationOfJurisdictionRequest.builder()
            .claimId(CLAIM_ID_404)
            .tempStationOfJurisdiction("398")
            .build();
    PutTempStationOfJurisdictionResponse response =
        sendAndReceive(putTempStationOfJurisdictionQueue, request);
    assertBaseResponseIsNot2xx(response, HttpStatus.NOT_FOUND);
  }

  @Test
  void testPutTempStationOfJurisdiction_500() {
    PutTempStationOfJurisdictionRequest request =
        PutTempStationOfJurisdictionRequest.builder()
            .claimId(CLAIM_ID_500)
            .tempStationOfJurisdiction("398")
            .build();
    PutTempStationOfJurisdictionResponse response =
        sendAndReceive(putTempStationOfJurisdictionQueue, request);
    assertBaseResponseIsNot2xx(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
