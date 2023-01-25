package gov.va.vro.mockbipce;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.model.bipevidence.BipFileProviderData;
import gov.va.vro.model.bipevidence.BipFileUploadPayload;
import gov.va.vro.model.bipevidence.UploadResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    BipFileProviderData updr = BipFileProviderData.builder()
        .contentSource("VRO")
        .dateVaReceivedDocument("2023-01-19")
        .documentTypeId(131)
        .build();

    BipFileUploadPayload payload = BipFileUploadPayload.builder()
        .providerData(updr)
        .contentName("example.pdf")
        .build();

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
