package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.controller.BaseControllerTest;
import gov.va.vro.service.provider.mas.service.MasAuthToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MasServiceTest extends BaseControllerTest {

  @Autowired MasAuthToken masAuthToken;

  @Test
  void testService() {
    assertNotNull(masAuthToken.getAuthorizedClientServiceAndManager());
  }
}
