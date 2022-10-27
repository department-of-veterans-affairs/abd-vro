package gov.va.vro.controller;

import gov.va.vro.api.resources.MasResource;
import gov.va.vro.api.responses.MasClaimResponse;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasExamOrderStatusPayload;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.MasDelays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Profile("!qa & !sandbox & !prod")
public class MasController implements MasResource {

  private final CamelEntrance camelEntrance;

  private final MasDelays masDelays;

  /** Initiate MAS integration. */
  @Override
  public ResponseEntity<MasClaimResponse> automatedClaim(MasAutomatedClaimPayload payload) {
    log.info("Received MAS automated claim request with collection ID {}", payload.getCollectionId());
    camelEntrance.notifyAutomatedClaim(payload, masDelays.getMasProcessingInitialDelay());
    MasClaimResponse response =
        MasClaimResponse.builder()
            .id(Integer.toString(payload.getCollectionId()))
            .message("Message Received")
            .build();
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<MasClaimResponse> examOrderingStatus(MasExamOrderStatusPayload payload) {
    log.info("Received MAS order statues request with collection ID {}", payload.getCollectionId());
    String message = camelEntrance.examOrderingStatus(payload);
    MasClaimResponse response =
        MasClaimResponse.builder()
            .id(Integer.toString(payload.getCollectionId()))
            .message(message)
            .build();
    return ResponseEntity.ok(response);
  }
}
