package gov.va.vro.controller;

import gov.va.vro.api.resources.MasResource;
import gov.va.vro.api.responses.MasResponse;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasExamOrderStatusPayload;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.MasConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MasController implements MasResource {

  private final CamelEntrance camelEntrance;

  private final MasConfig masConfig;

  /** Initiate MAS integration. */
  @Override
  public ResponseEntity<MasResponse> automatedClaim(MasAutomatedClaimPayload payload) {
    log.info(
        "Received MAS automated claim request with collection ID {}", payload.getCollectionId());
    String message;
    if (payload.isInScope()) {
      camelEntrance.notifyAutomatedClaim(
          payload, masConfig.getMasProcessingInitialDelay(), masConfig.getMasRetryCount());
      message = "Received";
    } else {
      // send slack notification
      var auditEvent = buildAuditEvent(payload);
      camelEntrance.sendSlack(auditEvent);
      message = "Out of scope";
    }
    MasResponse response =
        MasResponse.builder()
            .id(Integer.toString(payload.getCollectionId()))
            .message(message)
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

  private static AuditEvent buildAuditEvent(MasAutomatedClaimPayload payload) {
    return AuditEvent.builder()
        .eventId(Integer.toString(payload.getCollectionId()))
        .payloadType(MasAutomatedClaimPayload.class)
        .routeId("/automatedClaim")
        .message(
            String.format(
                "Request with collection Id %s is not in scope.", payload.getCollectionId()))
        .build();
  }
}
