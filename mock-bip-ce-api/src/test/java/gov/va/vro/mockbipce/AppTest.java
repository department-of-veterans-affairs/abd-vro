package gov.va.vro.mockbipce;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.mockbipce.model.EvidenceFile;
import gov.va.vro.mockbipce.repository.EvidenceFileRepository;
import gov.va.vro.model.bipevidence.BipFileProviderData;
import gov.va.vro.model.bipevidence.BipFileUploadPayload;
import gov.va.vro.model.bipevidence.response.UploadResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
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
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@Slf4j
@ActiveProfiles("test")
public class AppTest {
  @LocalServerPort int port;

  @Autowired
  @Qualifier("httpsRestTemplate")
  private RestTemplate restTemplate;

  @Autowired
  private EvidenceFileRepository repository;

  @Autowired
  private JwtGenerator jwtGenerator;

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

  @SneakyThrows
  private void postFileCommon(TestSpec spec) {
    final String veteranFileNumber = spec.getVeteranFileNumber();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    headers.set("X-Folder-URI", "FILENUMBER:" + veteranFileNumber);
    String jwt = jwtGenerator.generate();
    headers.set("Authorization", "Bearer " + jwt);

    BipFileProviderData updr =
        BipFileProviderData.builder()
            .contentSource("VRO")
            .dateVaReceivedDocument("2023-01-19")
            .documentTypeId(131)
            .build();

    final String filename = spec.getFileName();
    BipFileUploadPayload payload =
        BipFileUploadPayload.builder().providerData(updr).contentName(filename).build();

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("payload", payload);

    byte[] fileContent = spec.getFileContent().getBytes();
    Path testFile = Files.createTempFile("test-file", ".txt");
    Files.write(testFile, fileContent);
    FileSystemResource fsr = new FileSystemResource(testFile.toFile());
    body.add("file", fsr);

    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

    String url = spec.getUrl("/files");
    ResponseEntity<UploadResponse> response =
        restTemplate.postForEntity(url, request, UploadResponse.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    UploadResponse ur = response.getBody();
    log.info("UUID: " + ur.getUuid());

    verifyFile(spec);
  }

  @Test
  void postFileTest() {
    TestSpec spec = TestSpec.builder()
        .veteranFileNumber("763789990")
        .fileContent("Hello World !!, This is a test file.")
        .fileName("example.pdf")
        .port(port)
        .build();
    postFileCommon(spec);
  }

}
