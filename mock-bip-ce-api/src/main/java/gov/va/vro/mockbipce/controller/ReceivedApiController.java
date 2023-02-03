package gov.va.vro.mockbipce.controller;

import gov.va.vro.mockbipce.api.ReceivedApi;
import gov.va.vro.mockbipce.config.BasicStore;
import gov.va.vro.mockbipce.model.EvidenceFile;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Controller
@Slf4j
public class ReceivedApiController implements ReceivedApi {
  @Autowired private BasicStore store;

  @SneakyThrows
  @Override
  public ResponseEntity<byte[]> download(String fileNumber) {
    log.info("Entered download for fileNumber: {}", fileNumber);
    EvidenceFile evidenceFile = store.get(fileNumber);
    if (evidenceFile == null) {
      log.info("No file found for fileNumber: {}", fileNumber);
      HttpStatus status = HttpStatus.NOT_FOUND;
      throw new ResponseStatusException(status, status.getReasonPhrase());
    }
    byte[] content = evidenceFile.getContent();
    String filename = evidenceFile.getPayload().getContentName();

    ContentDisposition disposition = ContentDisposition.attachment().filename(filename).build();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDisposition(disposition);

    return new ResponseEntity<>(content, headers, HttpStatus.OK);
  }
}
