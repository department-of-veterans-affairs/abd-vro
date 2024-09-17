package gov.va.vro.bip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.bip.model.BipPayloadRequest;
import gov.va.vro.bip.model.contentions.GetSpecialIssueTypesRequest;
import gov.va.vro.bip.model.contentions.GetSpecialIssueTypesResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public class SpecialIssueTypesTest extends BaseIntegrationTest {

  @Test // g :svc-bip-api:integrationTest --tests
  // gov.va.vro.bip.SpecialIssueTypesTest.testGetSpecialIssueTypes_200
  void testGetSpecialIssueTypes_200() {
    BipPayloadRequest request = GetSpecialIssueTypesRequest.builder().build();
    GetSpecialIssueTypesResponse response = sendAndReceive(getSpecialIssueTypesQueue, request);
    assertBaseResponseIs2xx(response, HttpStatus.OK);
    var codeNamePairs = response.getCodeNamePairs();
    assertTrue(codeNamePairs.length > 0, "Special Issue Types response was empty");
    var emptyNameCount =
        Arrays.stream(codeNamePairs).filter(pair -> StringUtils.isEmpty(pair.getName())).count();
    assertEquals(emptyNameCount, 0, "Special Issue Types contained entries without names");
    var emptyCodeCount =
        Arrays.stream(codeNamePairs).filter(pair -> StringUtils.isEmpty(pair.getCode())).count();
    assertEquals(emptyCodeCount, 0, "Special Issue Types contained entries without codes");
  }
}
