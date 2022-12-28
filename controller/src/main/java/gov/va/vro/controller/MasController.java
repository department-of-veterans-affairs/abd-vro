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

import java.util.UUID;

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
    String correlationId = UUID.randomUUID().toString();
    payload.setCorrelationId(correlationId);
    String message = masProcessingService.processIncomingClaim(payload);
    MasResponse response = MasResponse.builder().id(correlationId).message(message).build();
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<MasResponse> examOrderingStatus(MasExamOrderStatusPayload payload) {
    int collectionId = payload.getCollectionId();
    log.info("Received MAS order status request with collection ID {}", collectionId);
    String correlationId = UUID.randomUUID().toString();
    payload.setCorrelationId(correlationId);
    masProcessingService.examOrderingStatus(payload);
    String message = String.format("Received Exam Order Status for collection Id %d.", collectionId);
    MasResponse response = MasResponse.builder().id(correlationId).message(message).build();
    return ResponseEntity.ok(response);
  }
}
