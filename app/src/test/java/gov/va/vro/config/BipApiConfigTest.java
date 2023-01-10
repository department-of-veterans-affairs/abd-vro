package gov.va.vro.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

/** @author warren @Date 1/5/23 */
@ActiveProfiles("test")
@SpringBootTest
class BipApiConfigTest {

  @Autowired
  @Qualifier("bipCERestTemplate")
  private RestTemplate template;

  @Autowired
  @Qualifier("bipRestTemplate")
  private RestTemplate httpsTemplate;

  @Test
  public void testRestTemplate() throws Exception {

    assertNotNull(template);
    assertNotNull(httpsTemplate);

    BipApiConfig config = new BipApiConfig();
    try {
      RestTemplate temp = config.getHttpsRestTemplate(new RestTemplateBuilder());
      fail();
    } catch (Exception e) {
      assertTrue(e.getCause() instanceof NullPointerException);
    }

    try {
      config.setTrustStore("bipcert.jks");
      config.setPassword("bad");
      RestTemplate temp = config.getHttpsRestTemplate(new RestTemplateBuilder());
      fail();
    } catch (Exception e) {
      assertTrue(true);
    }
  }
}
