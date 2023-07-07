package gov.va.vro.mockbipce;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.model.evidence.response.UploadResponse;
import gov.va.vro.bip.model.evidence.response.VefsErrorResponse;
import gov.va.vro.mockbipce.config.TestConfig;
import gov.va.vro.mockbipce.util.TestHelper;
import gov.va.vro.mockbipce.util.TestSpec;
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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
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
    String url = spec.getReceivedFilesUrl();
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

    ResponseEntity<UploadResponse> response = helper.postFiles(spec);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    UploadResponse ur = response.getBody();
    log.info("UUID: " + ur.getUuid());

    verifyFile(spec);

    String url = spec.getReceivedFilesUrl();
    restTemplate.delete(url);

    try {
      restTemplate.getForEntity(url, byte[].class);
      fail("Expected 404 error.");
    } catch (HttpStatusCodeException exception) {
      HttpStatus statusCode = exception.getStatusCode();
      assertEquals(HttpStatus.NOT_FOUND, statusCode);
    }
  }

  private void auxRunTest(TestSpec spec) {
    try {
      helper.postFiles(spec);
      fail("Expected 400 error");
    } catch (HttpStatusCodeException exception) {
      HttpStatus statusCode = exception.getStatusCode();
      assertEquals(HttpStatus.BAD_REQUEST, statusCode);
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
  void postFilesWrongIdTypeTest() {
    TestSpec spec = TestSpec.getBasicExample();
    spec.setPort(port);
    spec.setIdType("PARTICIPANT_ID");
    spec.setVeteranFileNumber("123459");

    auxRunTest(spec);
  }

  @Test
  void postFilesNoFolderUrlTest() {
    TestSpec spec = TestSpec.getBasicExample();
    spec.setPort(port);
    spec.setIgnoreFolderUri(true);
    spec.setVeteranFileNumber("145459");

    auxRunTest(spec);
  }

  @Test
  void postFilesInvalidFolderUrlTest() {
    TestSpec spec = TestSpec.getBasicExample();
    spec.setPort(port);
    spec.setVeteranFileNumber("");

    auxRunTest(spec);
  }
}
