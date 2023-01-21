package gov.va.vro.mockbipce;

import gov.va.vro.model.bip.BipFileProviderData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/mock-bip-ce")
public class Controller {
  @PostMapping(value = "files", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<BipCeFileUploadResponse> postFile(@RequestBody BipFileProviderData data) {
    log.info("Entered mock-bip-ce post file");
    BipCeFileUploadResponse response =
        BipCeFileUploadResponse.builder().owner("VETERAN:FILENUMBER:987267855").build();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
