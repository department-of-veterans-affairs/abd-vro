package gov.va.vro.controller;

import gov.va.vro.api.resources.MasResource;
import gov.va.vro.api.responses.MasResponse;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasExamOrderStatusPayload;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.MasConfig;
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

  private final MasConfig masConfig;

  /** Initiate MAS integration. */
  @Override
  public ResponseEntity<MasResponse> automatedClaim(MasAutomatedClaimPayload payload) {
    log.info(
        "Received MAS automated claim request with collection ID {}", payload.getCollectionId());
    camelEntrance.notifyAutomatedClaim(
        payload, masConfig.getMasProcessingInitialDelay(), masConfig.getMasRetryCount());
    MasResponse response =
        MasResponse.builder()
            .id(Integer.toString(payload.getCollectionId()))
            .message("Received")
            .build();
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<MasResponse> examOrderingStatus(MasExamOrderStatusPayload payload) {
    log.info("Received MAS order statues request with collection ID {}", payload.getCollectionId());
    camelEntrance.examOrderingStatus(payload);
    MasResponse response =
        MasResponse.builder()
            .id(Integer.toString(payload.getCollectionId()))
            .message("Received")
            .build();
    return ResponseEntity.ok(response);
  }
}
