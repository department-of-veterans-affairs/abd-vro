package gov.va.vro.abddataaccess.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.abddataaccess.model.AbdClaim;
import gov.va.vro.abddataaccess.model.AbdResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AbdRabbitReceiverTest {

  private static final String TEST_ICN = "9000682";
  private static final String TEST_DCODE = "7101";
  private static final String TEST_CLAIM_ID = "1234";

  @Autowired private AbdRabbitReceiver receiver;

  @Test
  public void testReceiveMessage() {
    AbdClaim claim01 = new AbdClaim(TEST_ICN, TEST_DCODE, TEST_CLAIM_ID);
    AbdResponse response = receiver.receiveMessage(claim01);
    assertNotNull(response);
    assertEquals(TEST_ICN, response.getVeteranIcn());
    assertEquals(TEST_DCODE, response.getDiagnosticCode());
    assertNotNull(response.getEvidence());
  }
}
