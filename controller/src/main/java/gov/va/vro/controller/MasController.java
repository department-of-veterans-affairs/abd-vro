package gov.va.vro.controller;

import gov.va.vro.api.resources.MasResource;
import gov.va.vro.api.responses.MasClaimResponse;
import gov.va.vro.model.mas.MasClaimDetailsPayload;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.MasDelays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MasController implements MasResource {

  private final CamelEntrance camelEntrance;

  private final MasDelays masDelays;

  /** Initiate MAS integration */
  @Override
  public ResponseEntity<MasClaimResponse> notifyAutomatedClaimDetails(
      MasClaimDetailsPayload payload) {
    log.info("Received MAS request with collection ID {}", payload.getCollectionsId());
    camelEntrance.notifyAutomatedClaim(payload, masDelays.getMasProcessingInitialDelay());
    MasClaimResponse response =
        MasClaimResponse.builder()
            .id(payload.getCollectionsId())
            .message("Message Received")
            .build();
    return ResponseEntity.ok(response);
  }
}
