package gov.va.vro.mockbipce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mockbipce.api.FilesApi;
import gov.va.vro.mockbipce.model.EvidenceFile;
import gov.va.vro.mockbipce.repository.EvidenceFileRepository;
import gov.va.vro.model.bipevidence.BipFileUploadPayload;
import gov.va.vro.model.bipevidence.response.UploadResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Controller
@Slf4j
public class FilesApiController implements FilesApi {
  @Autowired private EvidenceFileRepository repository;

  @SneakyThrows
  @Override
  public ResponseEntity<UploadResponse> upload(
      String xFolderUri, String payload, MultipartFile file) {
    String filename = file.getOriginalFilename();
    log.info("File {} being written to temp location.", filename);
    String targetName = FilenameUtils.getBaseName(filename);
    String extension = FilenameUtils.getExtension(filename);
    Path testFile = Files.createTempFile(targetName, "." + extension);
    Files.write(testFile, file.getBytes());
    log.info("Temp file is written to {}.", testFile.toString());

    String[] folderInfo = xFolderUri.split(":");
    if (folderInfo.length < 2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid X-Folder-URI header");
    }
    if (!"FILENUMBER".equals(folderInfo[0])) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only file numbers supported");
    }

    ObjectMapper mapper = new ObjectMapper();
    BipFileUploadPayload payloadObj = mapper.readValue(payload, BipFileUploadPayload.class);
    EvidenceFile evidenceFile = new EvidenceFile();
    evidenceFile.setId(folderInfo[1]);
    evidenceFile.setUuid(UUID.randomUUID());
    evidenceFile.setPayload(payloadObj);
    evidenceFile.setContent(file.getBytes());
    repository.save(evidenceFile);

    log.info("========= Payload Start ======");
    log.info(xFolderUri);
    log.info(payload);
    log.info("========== Payload End ========");

    String uuidValue = evidenceFile.getUuid().toString();
    UploadResponse ur = UploadResponse.builder().uuid(uuidValue).build();
    return new ResponseEntity<>(ur, HttpStatus.OK);
  }
}
