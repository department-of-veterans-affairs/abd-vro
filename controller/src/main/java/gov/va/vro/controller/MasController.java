package gov.va.vro.controller;

import gov.va.vro.api.requests.MasClaimDetailsRequest;
import gov.va.vro.api.responses.MasClaimResponse;
import gov.va.vro.api.resources.MasResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MasController implements MasResource {

  @Override
  public ResponseEntity<MasClaimResponse> notifyAutomatedClaimDetails(
      MasClaimDetailsRequest request) {
    log.info("Received MAS request with collection ID {}", request.getCollectionsId());
    // TODO: generate unique correlation ID
    String correlationId = "123";
    // TODO: Poll periodically to check for more details
    // TODO: Collect evidence, maybe call Lighthouse, maybe generate PDF
    MasClaimResponse response =
        MasClaimResponse.builder().id(correlationId).message("Received").build();
    return ResponseEntity.ok(response);
  }
}
