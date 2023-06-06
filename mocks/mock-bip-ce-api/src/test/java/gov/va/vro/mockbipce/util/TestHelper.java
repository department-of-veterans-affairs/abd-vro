package gov.va.vro.mockbipce.util;

import gov.va.vro.mockshared.jwt.JwtGenerator;
import gov.va.vro.bip.model.evidence.BipFileProviderData;
import gov.va.vro.bip.model.evidence.BipFileUploadPayload;
import gov.va.vro.bip.model.evidence.response.UploadResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class TestHelper {
  @Autowired
  @Qualifier("httpsRestTemplate")
  private RestTemplate restTemplate;

  @Autowired private JwtGenerator jwtGenerator;

  /**
   * Posts the file specified by spec.
   *
   * @param spec Test Specification
   * @return Response Entity
   */
  @SneakyThrows
  public ResponseEntity<UploadResponse> postFiles(TestSpec spec) {
    final String veteranFileNumber = spec.getVeteranFileNumber();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    if (!spec.isIgnoreFolderUri()) {
      String idType = spec.getIdType();
      headers.set("X-Folder-URI", "VETERAN:" + idType + ":" + veteranFileNumber);
    }
    if (!spec.isIgnoreJwt()) {
      String jwt = jwtGenerator.generate();
      log.info("jwt generated: {}", jwt);
      headers.set("Authorization", "Bearer " + jwt);
    }

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
    return restTemplate.postForEntity(url, request, UploadResponse.class);
  }
}
