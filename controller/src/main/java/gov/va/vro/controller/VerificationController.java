package gov.va.vro.controller;

import gov.va.vro.api.resources.VerificationResource;
import gov.va.vro.api.responses.BipVerificationResponse;
import gov.va.vro.service.provider.bip.service.IBipApiService;
import gov.va.vro.service.provider.bip.service.IBipCeApiService;
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
  private final IBipApiService bipApiService;
  private final IBipCeApiService bipCeApiService;

  @Override
  public ResponseEntity<BipVerificationResponse> bipVerificationTest() {
    boolean result =
        bipApiService.verifySpecialIssueTypes() && bipCeApiService.verifyDocumentTypes();
    BipVerificationResponse response = new BipVerificationResponse(result);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
