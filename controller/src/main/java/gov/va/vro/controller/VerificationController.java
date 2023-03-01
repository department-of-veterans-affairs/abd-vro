package gov.va.vro.controller;

import gov.va.vro.api.resources.VerificationResource;
import gov.va.vro.api.responses.BipVerificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class VerificationController implements VerificationResource {
  @Override
  public ResponseEntity<BipVerificationResponse> bipVerificationTest() {
    BipVerificationResponse response = new BipVerificationResponse(true);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
