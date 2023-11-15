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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Unit tests for LighthouseApiService.
 *
 * @author warren @Date 8/30/22
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@ContextConfiguration(classes = LighthouseProperties.class)
@TestPropertySource({"classpath:application.yaml", "classpath:application-test.yaml"})
@ExtendWith(MockitoExtension.class)
@Slf4j
class LighthouseApiServiceTest {

  private static final String TEST_PATIENT = "9000682";
  private static final String TEST_TOKEN = "lighthouseToken.json";

  @Mock private RestTemplate restTemplate;

  @Autowired private LighthouseProperties lhProps;

  private LighthouseTokenMessage testToken;

  private void setupMocking() {

    try {
      String filename =
          Objects.requireNonNull(getClass().getClassLoader().getResource(TEST_TOKEN)).getPath();
      Path tokenFilePath = Path.of(filename);
      String token = Files.readString(tokenFilePath);

      ResponseEntity<String> resp = ResponseEntity.ok(token);

      Mockito.doReturn(resp)
          .when(restTemplate)
          .postForEntity(
              ArgumentMatchers.eq(lhProps.getTokenurl()),
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
    LighthouseApiService service = new LighthouseApiService(lhProps, restTemplate);
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
