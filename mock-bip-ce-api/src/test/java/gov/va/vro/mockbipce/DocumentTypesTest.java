package gov.va.vro.mockbipce;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.mockbipce.config.TestConfig;
import gov.va.vro.mockshared.jwt.JwtGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@Slf4j
@ActiveProfiles("test")
public class DocumentTypesTest {
  @LocalServerPort int port;

  @Autowired
  @Qualifier("httpsRestTemplate")
  private RestTemplate restTemplate;

  @Autowired private JwtGenerator jwtGenerator;

  @Test
  void getDocumentTypesTest() {
    HttpHeaders headers = new HttpHeaders();
    String jwt = jwtGenerator.generate();
    log.info("jwt generated: {}", jwt);
    headers.set("Authorization", "Bearer " + jwt);

    HttpEntity<Void> entity = new HttpEntity<>(headers);
    String url = "https://localhost:" + port + "/documentTypes";
    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    String body = response.getBody();
    assertEquals("[]", body);
  }
}
