package gov.va.vro.bip.config;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

/**
 * BIP API configuration tests.
 *
 * @author warren @Date 1/5/23
 */
@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource({"classpath:application.yaml", "classpath:application-test.yaml"})
@Slf4j
class BipApiConfigTest {

  @Autowired
  @Qualifier("bipCERestTemplate")
  private RestTemplate template;

  @Autowired private BipApiConfig config;

  @Test
  public void testRestTemplate() {
    try {
      // InputStream sourceStream = getClass().getClassLoader().getResourceAsStream("bipcert.jks");
      // String store = new String(sourceStream.readAllBytes());
      // config.setTrustStore(store);
      // config.setKeystore(store);
      // config.setPassword("vropassword");
      assertEquals(config.getPassword(), "keystore_pw", "This failure indicates that config is no longer loading according to spring's layered configuration behavior");
      assertNotNull(config.getTruststore());
      assertNotNull(config.getKeystore());
      RestTemplate template = config.getHttpsRestTemplate(new RestTemplateBuilder());
      assertNotNull(template);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Test failed due to exception: " + e.getMessage());
    }
  }
}
