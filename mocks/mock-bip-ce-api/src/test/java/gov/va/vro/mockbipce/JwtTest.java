package gov.va.vro.mockbipce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.model.evidence.response.VefsErrorResponse;
import gov.va.vro.mockbipce.config.TestConfig;
import gov.va.vro.mockbipce.util.TestHelper;
import gov.va.vro.mockbipce.util.TestSpec;
import gov.va.vro.mockshared.jwt.JwtSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@Slf4j
@ActiveProfiles("test")
public class JwtTest {
  @LocalServerPort int port;

  @Autowired private TestHelper helper;

  @SpyBean private JwtSpecification props;

  private void auxRunTest(TestSpec spec) {
    try {
      helper.postFiles(spec);
      fail("Expected 401 error");
    } catch (HttpStatusCodeException exception) {
      HttpStatus statusCode = exception.getStatusCode();
      assertEquals(HttpStatus.UNAUTHORIZED, statusCode);
      ObjectMapper mapper = new ObjectMapper();
      try {
        mapper.readValue(exception.getResponseBodyAsString(), VefsErrorResponse.class);
      } catch (Exception jsonException) {
        fail("Expected a VefsErrorResponse object", jsonException);
      }
    } catch (RestClientException exception) {
      fail("Unexpected runtime exception", exception);
    }
  }

  @Test
  void invalidJwtSecretTest() {
    Mockito.when(props.getSecret()).thenReturn("Not the secret");

    TestSpec spec = TestSpec.getBasicExample();
    spec.setPort(port);

    auxRunTest(spec);
  }

  @Test
  void noJwtTest() {
    TestSpec spec = TestSpec.getBasicExample();
    spec.setPort(port);
    spec.setIgnoreJwt(true);

    auxRunTest(spec);
  }
}
