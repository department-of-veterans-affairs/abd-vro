package gov.va.vro.service.provider.mas.service;

import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasExamOrderStatusPayload;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.MasConfig;
import gov.va.vro.service.provider.bip.service.BipClaimService;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MasProcessingService {

  private final CamelEntrance camelEntrance;

  private final MasConfig masConfig;

  private final BipClaimService bipClaimService;

  /**
   * Processes incoming claim.
   *
   * @param payload mas payload.
   * @return String
   */
  public String processIncomingClaim(MasAutomatedClaimPayload payload) {

    if (!payload.isInScope()) {
      var message =
          String.format(
              "Request with [collection id = %s], [diagnostic code = %s],"
                  + " and [disability action type = %s] is not in scope.",
              payload.getCollectionId(),
              payload.getDiagnosticCode(),
              payload.getDisabilityActionType());
      offRampClaim(payload, message);
      return "Out of scope";
    }
    if (!bipClaimService.hasAnchors(payload.getCollectionId())) {
      var message =
          String.format(
              "Request with [collection id = %s] does not qualify for"
                  + " automated processing because it is missing anchors",
              payload.getCollectionId());
      log.info(message);
      offRampClaim(payload, message);
      return "Missing anchor";
    }
    camelEntrance.notifyAutomatedClaim(
        payload, masConfig.getMasProcessingInitialDelay(), masConfig.getMasRetryCount());
    return "Received";
  }

  public void examOrderingStatus(MasExamOrderStatusPayload payload) {
    camelEntrance.examOrderingStatus(payload);
  }

  private void offRampClaim(MasAutomatedClaimPayload payload, String message) {
    var auditEvent = buildAuditEvent(payload, message);
    camelEntrance.sendSlack(auditEvent);
    var mpo = new MasProcessingObject();
    mpo.setClaimPayload(payload);
    camelEntrance.offRampClaim(mpo);
  }

  private static AuditEvent buildAuditEvent(MasAutomatedClaimPayload payload, String message) {
    return AuditEvent.builder()
        .eventId(Integer.toString(payload.getCollectionId()))
        .payloadType(MasAutomatedClaimPayload.class)
        .routeId("/automatedClaim")
        .message(message)
        .build();
  }
}
