package gov.va.vro.mockbipce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mockbipce.api.FilesApi;
import gov.va.vro.mockbipce.model.store.BasicStore;
import gov.va.vro.mockbipce.model.store.EvidenceFile;
import gov.va.vro.model.bipevidence.BipFileUploadPayload;
import gov.va.vro.model.bipevidence.response.UploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
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
@RequiredArgsConstructor
public class FilesApiController implements FilesApi {
  private final BasicStore store;

  @SneakyThrows
  @Override
  public ResponseEntity<UploadResponse> upload(
      String folderUri, String payload, MultipartFile file) {
    String filename = file.getOriginalFilename();
    log.info("File {} being written to temp location.", filename);
    String targetName = FilenameUtils.getBaseName(filename);
    String extension = FilenameUtils.getExtension(filename);
    Path testFile = Files.createTempFile(targetName, "." + extension);
    Files.write(testFile, file.getBytes());
    log.info("Temp file is written to {}.", testFile.toString());

    if (folderUri == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No X-Folder-URI header");
    }
    String[] folderInfo = folderUri.split(":");
    if (folderInfo.length < 3) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid X-Folder-URI header");
    }
    if (!("VETERAN".equals(folderInfo[0]) && "FILENUMBER".equals(folderInfo[1]))) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Only veteran file numbers supported");
    }

    if ("9999390".equals(folderInfo[2]) || "9999392".equals(folderInfo[2])) { // This patient is used for BIP exception testing
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Returning 500 for testing");
    }

    ObjectMapper mapper = new ObjectMapper();
    BipFileUploadPayload payloadObj = mapper.readValue(payload, BipFileUploadPayload.class);
    EvidenceFile evidenceFile = new EvidenceFile();
    evidenceFile.setFileNumber(folderInfo[2]);
    evidenceFile.setUuid(UUID.randomUUID());
    evidenceFile.setPayload(payloadObj);
    evidenceFile.setContent(file.getBytes());
    store.put(evidenceFile);

    log.info("========= Payload Start ======");
    log.info(folderUri);
    log.info(payload);
    log.info("========== Payload End ========");

    String uuidValue = evidenceFile.getUuid().toString();
    UploadResponse ur = UploadResponse.builder().uuid(uuidValue).build();
    return new ResponseEntity<>(ur, HttpStatus.OK);
  }
}
