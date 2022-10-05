package gov.va.vro.abddataaccess.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.abddataaccess.config.properties.LighthouseProperties;
import gov.va.vro.abddataaccess.exception.AbdException;
import gov.va.vro.abddataaccess.model.LighthouseTokenMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Unit tests for LighthouseApiService.
 *
 * @author warren @Date 8/30/22
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
@ActiveProfiles("test")
class LighthouseApiServiceTest {

  private static final String TEST_PATIENT = "9000682";
  private static final String CLIENTID = "00ah28rb5jLj4NeSN2p7";
  private static final String ASSERTIONURL =
      "https://deptva-eval.okta.com/oauth2/aus8nm1q0f7VQ0a482p7/v1/token";
  private static final String TOKENURL = "https://sandbox-api.va.gov/oauth2/health/system/v1/token";
  private static final String TEST_KEY_FILE = "testkey.pem";
  private static final String TEST_TOKEN = "lighthouseToken.json";

  @InjectMocks private LighthouseApiService service;

  @Mock private RestTemplate restTemplate;

  @Mock private LighthouseProperties lhProps;

  private LighthouseTokenMessage testToken;

  private void setupMocking() {

    try {
      String filename =
          Objects.requireNonNull(getClass().getClassLoader().getResource(TEST_KEY_FILE)).getPath();
      File keyfile = new File(filename);
      String key = Files.readString(keyfile.toPath());

      Mockito.doReturn(ASSERTIONURL).when(lhProps).getAssertionurl();
      Mockito.doReturn(TOKENURL).when(lhProps).getTokenurl();
      Mockito.doReturn(CLIENTID).when(lhProps).getClientId();
      Mockito.doReturn(key).when(lhProps).getPemkey();

      filename =
          Objects.requireNonNull(getClass().getClassLoader().getResource(TEST_TOKEN)).getPath();
      Path tokenFilePath = Path.of(filename);
      String token = Files.readString(tokenFilePath);

      ResponseEntity<String> resp = ResponseEntity.ok(token);

      Mockito.doReturn(resp)
          .when(restTemplate)
          .postForEntity(
              ArgumentMatchers.eq(TOKENURL),
              ArgumentMatchers.any(HttpEntity.class),
              ArgumentMatchers.eq(String.class));

      ObjectMapper mapper = new ObjectMapper();
      testToken = mapper.readValue(token, LighthouseTokenMessage.class);
    } catch (IOException e) {
      log.error("Failed to parse lighthouse token message.", e);
      fail("Mocking");
    } catch (NullPointerException e) {
      log.error("Failed to get file path.", e);
      fail("Mocking");
    }
  }

  @Test
  public void testGetLighthouseToken() throws AbdException {
    setupMocking();
    String resp = service.getLighthouseToken(AbdDomain.BLOOD_PRESSURE, TEST_PATIENT);
    assertEquals(testToken.getTokenType() + " " + testToken.getAccessToken(), resp);
  }

  @Test
  public void testScope() {
    for (AbdDomain domain : AbdDomain.values()) {
      log.info(domain.getScope());
      assertTrue(domain.getScope().startsWith("launch patient"));
    }
  }
}
