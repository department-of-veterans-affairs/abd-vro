package gov.va.vro.bip.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.service.BipException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.List;

/**
 * BIP API configuration tests.
 *
 * @author warren @Date 1/5/23
 */
class BipApiConfigTest {

  private BipApiConfig config;
  private JacksonConfig jacksonConfig;

  @BeforeEach
  public void setUp() {
    config = new BipApiConfig();
    jacksonConfig = new JacksonConfig();
  }

  @Test
  public void testGetHttpsRestTemplate_WithoutConfiguringCerts() {
    try {
      config.getHttpsRestTemplate(
          List.of(jacksonConfig.mappingJackson2HttpMessageConverter(new ObjectMapper())));
      fail();
    } catch (Exception e) {
      assertTrue(e.getCause() instanceof NullPointerException);
    }
  }

  @Test
  public void testGetHttpsRestTemplate_WithBadCerts() {
    Assertions.assertThrows(
        BipException.class,
        () -> {
          config.setTrustStore("biptruststore.jks");
          config.setKeystore("biptruststore.jks");
          config.setPassword("bad");
          config.getHttpsRestTemplate(
              List.of(jacksonConfig.mappingJackson2HttpMessageConverter(new ObjectMapper())));
        });
  }

  @Test
  public void testGetHttpsRestTemplate_WithoutTrustStore() {
    config.setTrustStore("");
    config.setPassword("");
    RestTemplate temp =
        config.getHttpsRestTemplate(
            List.of(jacksonConfig.mappingJackson2HttpMessageConverter(new ObjectMapper())));
    assertNotNull(temp);
  }

  @Test
  public void testGetHttpsRestTemplate_WithValidCerts() {
    try (InputStream sourceStream =
        getClass().getClassLoader().getResourceAsStream("bipcert.jks")) {
      assertNotNull(sourceStream);
      String store = new String(sourceStream.readAllBytes());
      config.setTrustStore(store);
      config.setKeystore(store);
      config.setPassword("vropassword");
      RestTemplate template =
          config.getHttpsRestTemplate(
              List.of(jacksonConfig.mappingJackson2HttpMessageConverter(new ObjectMapper())));

      assertNotNull(template);
    } catch (Exception e) {
      fail();
    }
  }
}
