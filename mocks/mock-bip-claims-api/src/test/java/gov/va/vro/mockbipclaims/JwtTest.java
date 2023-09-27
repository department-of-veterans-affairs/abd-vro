package gov.va.vro.mockbipclaims;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mockbipclaims.config.TestConfig;
import gov.va.vro.mockbipclaims.model.bip.ProviderResponse;
import gov.va.vro.mockbipclaims.util.TestHelper;
import gov.va.vro.mockbipclaims.util.TestSpec;
import gov.va.vro.mockshared.jwt.JwtSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

  @BeforeEach
  void initJwtProps() {}

  private String getUrl(String endPoint) {
    String base = "https://localhost:" + port + "/";
    return base + endPoint;
  }

  private void auxRunTest(TestSpec spec) {
    try {
      helper.getClaim(spec);
      fail("Expected 401 error");
    } catch (HttpStatusCodeException exception) {
      HttpStatusCode statusCode = exception.getStatusCode();
      assertEquals(HttpStatus.UNAUTHORIZED, statusCode);
      ObjectMapper mapper = new ObjectMapper();
      try {
        mapper.readValue(exception.getResponseBodyAsString(), ProviderResponse.class);
      } catch (Exception jsonException) {
        fail("Expected a ProviderResponse object", jsonException);
      }
    } catch (RestClientException exception) {
      fail("Unexpected runtime exception", exception);
    }
  }

  @Test
  void invalidJwtSecretTest() {
    Mockito.when(props.getSecret()).thenReturn("Not the secret");

    TestSpec spec = new TestSpec();
    spec.setClaimId(1010);
    spec.setPort(port);

    auxRunTest(spec);
  }

  @Test
  void noJwtTest() {
    TestSpec spec = new TestSpec();
    spec.setClaimId(1010);
    spec.setPort(port);
    spec.setIgnoreJwt(true);

    auxRunTest(spec);
  }
}
