package gov.va.vro.mockbipce;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.mockbipce.config.TestConfig;
import gov.va.vro.mockbipce.util.TestHelper;
import gov.va.vro.mockbipce.util.TestSpec;
import gov.va.vro.model.bipevidence.response.UploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@Slf4j
@ActiveProfiles("test")
public class FilesTest {
  @LocalServerPort int port;

  @Autowired
  @Qualifier("httpsRestTemplate")
  private RestTemplate restTemplate;

  @Autowired private TestHelper helper;

  private void verifyFile(TestSpec spec) {
    String baseUrl = spec.getUrl("/received-files/");
    String url = baseUrl + spec.getVeteranFileNumber();
    ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

    byte[] content = spec.getFileContent().getBytes();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertArrayEquals(content, response.getBody());

    HttpHeaders headers = response.getHeaders();
    ContentDisposition disposition = headers.getContentDisposition();
    assertEquals(spec.getFileName(), disposition.getFilename());
  }

  @Test
  void postFilesPositiveTest() {
    TestSpec spec = TestSpec.getBasicExample();
    spec.setPort(port);

    ResponseEntity<UploadResponse> response = helper.postFiles(spec, UploadResponse.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    UploadResponse ur = response.getBody();
    log.info("UUID: " + ur.getUuid());

    verifyFile(spec);
  }
}
