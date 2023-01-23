package gov.va.vro.mockbipce;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.openapitools.model.Payload;
import org.openapitools.model.UploadProviderDataRequest;
import org.openapitools.model.UploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;

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

  @SneakyThrows
  private void postFileCommon(RestTemplate rt) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    UploadProviderDataRequest updr = new UploadProviderDataRequest();
    updr.setContentSource("VRO");
    updr.setDateVaReceivedDocument("2023-01-19");
    updr.documentTypeId(131);

    Payload payload = new Payload();
    payload.setProviderData(updr);
    payload.setContentName("example.pdf");

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("payLoad", payload);

    Path testFile = Files.createTempFile("test-file", ".txt");
    Files.write(testFile, "Hello World !!, This is a test file.".getBytes());
    FileSystemResource fsr = new FileSystemResource(testFile.toFile());
    body.add("file", fsr);

    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

    ResponseEntity<UploadResponse> response =
        rt.postForEntity("https://localhost:" + port + "/files", request, UploadResponse.class);

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
