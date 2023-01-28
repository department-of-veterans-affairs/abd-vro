package gov.va.vro.mockbipce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mockbipce.api.FilesApi;
import gov.va.vro.mockbipce.model.EvidenceFile;
import gov.va.vro.mockbipce.repository.EvidenceFileRepository;
import gov.va.vro.model.bipevidence.BipFileUploadPayload;
import gov.va.vro.model.bipevidence.response.UploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@Slf4j
public class FilesApiController implements FilesApi {
  @Autowired
  private EvidenceFileRepository repository;


  @Override
  public ResponseEntity<UploadResponse> upload(
      String xFolderUri, String payload, MultipartFile file) {
    try {
      byte[] content = file.getBytes();

      ObjectMapper mapper = new ObjectMapper();
      BipFileUploadPayload payloadObj = mapper.readValue(payload, BipFileUploadPayload.class);
      EvidenceFile evidenceFile = new EvidenceFile("34", payloadObj, content);
      repository.save(evidenceFile);

      String filename = file.getOriginalFilename();
      log.info("File {} being written to temp location.", filename);
      String targetName = FilenameUtils.getBaseName(filename);
      String extension = FilenameUtils.getExtension(filename);
      Path testFile = Files.createTempFile(targetName, "." + extension);
      Files.write(testFile, file.getBytes());
      log.info("Temp file is written to {}.", testFile.toString());
    } catch (IOException ex) {
      log.error("Unable to write the file", ex);
    }

    log.info("========= Payload Start ======");
    log.info(payload);
    log.info("========== Payload End ========");

    UploadResponse ur =
        UploadResponse.builder().uuid("046b6c7f-0b8a-43b9-b35d-6489e6daee91").build();
    return new ResponseEntity<>(ur, HttpStatus.OK);
  }
}
