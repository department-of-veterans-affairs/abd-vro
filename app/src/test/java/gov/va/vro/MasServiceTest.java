package gov.va.vro;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.config.MasOauth2Config;
import gov.va.vro.service.provider.mas.service.MasCollectionAnnotsApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MasServiceTest extends BaseControllerTest {

  @Autowired MasCollectionAnnotsApiService masCollectionAnnotsApiService;

  @Autowired ClientRegistrationRepository clientRegistrationRepository;

  @Autowired MasOauth2Config masOauth2Config;

  @Test
  void testService() {
    assertNotNull(masCollectionAnnotsApiService.getAuthorizedClientServiceAndManager());
    System.out.println(masOauth2Config);
  }
}
