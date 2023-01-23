package gov.va.vro.mockbipce.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.openapitools.model.Payload;
import org.openapitools.model.UploadResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Generated;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-01-22T14:21:59.944759-05:00[America/New_York]")
@Controller
@Slf4j
public class FilesApiController implements FilesApi {
  @Override
  public ResponseEntity<UploadResponse> upload(
      String xFolderURI, Payload payload, MultipartFile file) {
    try {
      String filename = file.getOriginalFilename();
      log.info("File {} being written to temp location.", filename);
      String targetName = FilenameUtils.getBaseName(filename);
      String extension = FilenameUtils.getExtension(filename);
      Path testFile = Files.createTempFile (targetName, "." + extension);
      Files.write(testFile, file.getBytes());
      log.info("Temp file is written to {}.", testFile.toString());
    } catch(IOException ex) {
      log.error("Unable to write the file", ex);
    }

    UploadResponse ur = new UploadResponse();
    ur.setOwner("VETERAN:FILENUMBER:987267855");
    return new ResponseEntity<>(ur, HttpStatus.OK);
  }
}
