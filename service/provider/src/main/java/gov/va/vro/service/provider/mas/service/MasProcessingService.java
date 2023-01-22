package gov.va.vro.service.provider.mas.service;

import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasExamOrderStatusPayload;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.MasConfig;
import gov.va.vro.service.provider.bip.service.BipClaimService;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import gov.va.vro.service.spi.db.SaveToDbService;
import gov.va.vro.service.spi.model.Claim;
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

  private final SaveToDbService saveToDbService;

  /**
   * Processes incoming claim.
   *
   * @param payload mas payload.
   * @return String
   */
  public String processIncomingClaim(MasAutomatedClaimPayload payload) {
    saveToDbService.insertClaim(toClaim(payload));
    if (!payload.isInScope()) {
      var message =
          String.format(
              "Request with [collection id = %s], [diagnostic code = %s],"
                  + " and [disability action type = %s] is not in scope.",
              payload.getCollectionId(),
              payload.getDiagnosticCode(),
              payload.getDisabilityActionType());
      offRampClaim(payload, message);
      return String.format(
          "Claim with collection Id %s is out of scope.", payload.getCollectionId());
    }

    if (payload.isPresumptive() != null && !payload.isPresumptive()) {
      var message =
          String.format(
              "Request with [collection id = %s], [diagnostic code = %s],"
                  + " [disability action type = %s] and [flashIds = %s] is not presumptive.",
              payload.getCollectionId(),
              payload.getDiagnosticCode(),
              payload.getDisabilityActionType(),
              payload.getVeteranFlashIds());
      offRampClaim(payload, message);
      return String.format(
          "Claim with collection Id %s is not presumptive.", payload.getCollectionId());
    }

    if (!bipClaimService.hasAnchors(payload.getCollectionId())) {
      var message =
          String.format(
              "Request with [collection id = %s] does not qualify for"
                  + " automated processing because it is missing anchors",
              payload.getCollectionId());
      log.info(message);
      offRampClaim(payload, message);
      return String.format(
          "Claim with collection Id %s is missing an anchor.", payload.getCollectionId());
    }
    camelEntrance.notifyAutomatedClaim(
        payload, masConfig.getMasProcessingInitialDelay(), masConfig.getMasRetryCount());
    return String.format("Received Claim for collection Id %d.", payload.getCollectionId());
  }

  public void examOrderingStatus(MasExamOrderStatusPayload payload) {
    camelEntrance.examOrderingStatus(payload);
  }

  private void offRampClaim(MasAutomatedClaimPayload payload, String message) {
    var auditEvent = buildAuditEvent(payload, message);
    camelEntrance.offrampClaim(auditEvent);
    var mpo = new MasProcessingObject();
    mpo.setClaimPayload(payload);
    camelEntrance.completeProcessing(mpo);
  }

  private static AuditEvent buildAuditEvent(MasAutomatedClaimPayload payload, String message) {
    return AuditEvent.builder()
        .eventId(Integer.toString(payload.getCollectionId()))
        .payloadType(payload.getDisplayName())
        .routeId("/automatedClaim")
        .message(message)
        .build();
  }

  private Claim toClaim(MasAutomatedClaimPayload payload) {
    return Claim.builder()
        .claimSubmissionId(Integer.toString(payload.getClaimId()))
        .collectionId(Integer.toString(payload.getCollectionId()))
        .diagnosticCode(payload.getDiagnosticCode())
        .veteranIcn(payload.getVeteranIcn())
        .build();
  }
}
