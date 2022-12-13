package gov.va.vro.controller;

import gov.va.vro.api.resources.MasResource;
import gov.va.vro.api.responses.MasResponse;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasExamOrderStatusPayload;
import gov.va.vro.service.provider.mas.service.MasProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MasController implements MasResource {

  private final MasProcessingService masProcessingService;

  /** Initiate MAS integration. */
  @Override
  public ResponseEntity<MasResponse> automatedClaim(MasAutomatedClaimPayload payload) {
    log.info(
        "Received MAS automated claim request with collection ID {}", payload.getCollectionId());
    String message = masProcessingService.processIncomingClaim(payload);
    MasResponse response =
        MasResponse.builder()
            .id(Integer.toString(payload.getCollectionId()))
            .message(message)
            .build();
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<MasResponse> examOrderingStatus(MasExamOrderStatusPayload payload) {
    int collectionId = payload.getCollectionId();
    log.info("Received MAS order status request with collection ID {}", collectionId);
    masProcessingService.examOrderingStatus(payload);
    MasResponse response =
        MasResponse.builder().id(Integer.toString(collectionId)).message("Received").build();
    return ResponseEntity.ok(response);
  }
}
