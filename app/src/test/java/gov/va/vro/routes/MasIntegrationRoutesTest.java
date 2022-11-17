package gov.va.vro.routes;

import static org.junit.jupiter.api.Assertions.fail;

import gov.va.vro.BaseIntegrationTest;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.CamelEntrance;
import org.apache.camel.CamelExecutionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MasIntegrationRoutesTest extends BaseIntegrationTest {

  @Autowired private CamelEntrance camelEntrance;

  @Test
  void processClaimInvalidInput() {
    var payload = MasAutomatedClaimPayload.builder().collectionId(123).build();
    try {
      camelEntrance.processClaim(payload);
      fail();
    } catch (CamelExecutionException cee) {

    }
  }
}
