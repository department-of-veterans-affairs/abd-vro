package gov.va.vro.mockbipce;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

  @Test
  void postFileTest() {
    BipFileProviderData request = BipFileProviderData.builder()
        .claimantLastName("Doe").claimantFirstName("Joe").build();

    ResponseEntity<BipCeFileUploadResponse> response = restTemplate.postForEntity(
      "https://localhost:" + port + "/mock-bip-ce/files", request, BipCeFileUploadResponse.class
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void postNoCertFileTest() {
    BipFileProviderData request = BipFileProviderData.builder()
        .claimantLastName("Doe").claimantFirstName("Joe").build();

    ResponseEntity<BipCeFileUploadResponse> response = restNoCertTemplate.postForEntity(
        "https://localhost:" + port + "/mock-bip-ce/files", request, BipCeFileUploadResponse.class
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
