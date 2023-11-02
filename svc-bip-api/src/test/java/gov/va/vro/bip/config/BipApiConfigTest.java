package gov.va.vro.bip.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import gov.va.vro.bip.service.BipException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

/**
 * BIP API configuration tests.
 *
 * @author warren @Date 1/5/23
 */
@ActiveProfiles("test")
class BipApiConfigTest {

  @Test
  public void testRestTemplate() {
    BipApiConfig config = new BipApiConfig();
    try {
      config.getHttpsRestTemplate(new RestTemplateBuilder());
      fail();
    } catch (Exception e) {
      assertTrue(e.getCause() instanceof NullPointerException);
    }

    try {
      config.setTrustStore("biptruststore.jks");
      config.setKeystore("biptruststore.jks");
      config.setPassword("bad");
      config.getHttpsRestTemplate(new RestTemplateBuilder());
      fail();
    } catch (BipException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    try {
      config.setTrustStore("");
      config.setPassword("");
      RestTemplate temp = config.getHttpsRestTemplate(new RestTemplateBuilder());
      assertNotNull(temp);
    } catch (Exception e) {
      fail();
    }

    try (InputStream sourceStream =
        getClass().getClassLoader().getResourceAsStream("bipcert.jks")) {
      assertNotNull(sourceStream);
      String store = new String(sourceStream.readAllBytes());
      config.setTrustStore(store);
      config.setKeystore(store);
      config.setPassword("vropassword");
      RestTemplate template = config.getHttpsRestTemplate(new RestTemplateBuilder());
      assertNotNull(template);
    } catch (Exception e) {
      fail();
    }
  }
}
