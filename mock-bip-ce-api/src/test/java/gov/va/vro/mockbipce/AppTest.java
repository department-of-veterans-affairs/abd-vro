package gov.va.vro.mockbipce;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.mockbipce.controller.BipCeFileUploadResponse;
import gov.va.vro.model.bip.BipFileProviderData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@ActiveProfiles("test")
public class AppTest {
  @LocalServerPort int port;

  @Autowired
  @Qualifier("httpsRestTemplate")
  private RestTemplate restTemplate;

  @Autowired
  @Qualifier("httpsNoCertificationRestTemplate")
  private RestTemplate restNoCertTemplate;

  private void postFileCommon(RestTemplate rt) {
    BipFileProviderData request =
        BipFileProviderData.builder().claimantLastName("Doe").claimantFirstName("Joe").build();

    ResponseEntity<BipCeFileUploadResponse> response =
        rt.postForEntity(
            "https://localhost:" + port + "/files",
            request,
            BipCeFileUploadResponse.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void postFileTest() {
    postFileCommon(restTemplate);
  }

  @Test
  void postNoCertFileTest() {
    postFileCommon(restNoCertTemplate);
  }
}
