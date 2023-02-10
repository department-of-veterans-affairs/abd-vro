package gov.va.vro.service.provider.mas.service;

import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasExamOrderStatusPayload;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.MasConfig;
import gov.va.vro.service.provider.bip.service.BipClaimService;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import gov.va.vro.service.spi.db.SaveToDbService;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.ExamOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MasProcessingService {

  private final CamelEntrance camelEntrance;

  private final MasConfig masConfig;

  private final BipClaimService bipClaimService;

  private final ClaimRepository claimRepository;

  private final SaveToDbService saveToDbService;

  /**
   * Processes incoming claim.
   *
   * @param payload mas payload.
   * @return String
   */
  public String processIncomingClaim(MasAutomatedClaimPayload payload) {
    saveToDbService.insertClaim(toClaim(payload));
    saveToDbService.insertFlashIds(payload.getVeteranFlashIds(), payload.getVeteranIcn());
    var offRampReasonOptional = getOffRampReason(payload);
    if (offRampReasonOptional.isPresent()) {
      var offRampReason = offRampReasonOptional.get();
      payload.setOffRampReason(offRampReason);
      saveToDbService.setOffRampReason(payload);
      offRampClaim(payload, offRampReason);
      return offRampReason;
    }
    camelEntrance.notifyAutomatedClaim(
        payload, masConfig.getMasProcessingInitialDelay(), masConfig.getMasRetryCount());
    return String.format("Received Claim for collection Id %d.", payload.getCollectionId());
  }

  private Optional<String> getOffRampReason(MasAutomatedClaimPayload payload) {
    if (!payload.isInScope()) {
      var message =
          String.format(
              "Claim with [collection id = %s], [diagnostic code = %s],"
                  + " and [disability action type = %s] is not in scope.",
              payload.getCollectionId(),
              payload.getDiagnosticCode(),
              payload.getDisabilityActionType());
      return Optional.of(message);
    }

    if (payload.isPresumptive() != null && !payload.isPresumptive()) {
      var message =
          String.format(
              "Claim with [collection id = %s], [diagnostic code = %s],"
                  + " [disability action type = %s] and [flashIds = %s] is not presumptive.",
              payload.getCollectionId(),
              payload.getDiagnosticCode(),
              payload.getDisabilityActionType(),
              payload.getVeteranFlashIds());
      return Optional.of(message);
    }

    long claimId = Long.parseLong(payload.getClaimDetail().getBenefitClaimId());
    log.info("Check hasAnchors for claim ID, {}", claimId); // TODO: remove it after test.
    if (!bipClaimService.hasAnchors(claimId)) {
      var message =
          String.format(
              "Claim with [collection id = %s] does not qualify for"
                  + " automated processing because it is missing anchors.",
              payload.getCollectionId());
      log.info(message);
      offRampClaim(payload, message);
      return Optional.of(message);
    }
    return Optional.empty();
  }

  public void examOrderingStatus(MasExamOrderStatusPayload payload) {
    saveToDbService.insertOrUpdateExamOrderingStatus(buildExamOrder(payload));
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
        .inScope(payload.isInScope())
        .disabilityActionType(payload.getDisabilityActionType())
        .offRampReason(payload.getOffRampReason())
        .submissionSource(payload.getClaimDetail().getClaimSubmissionSource())
        .submissionDate(OffsetDateTime.parse(payload.getClaimDetail().getClaimSubmissionDateTime()))
        .vbmsId(Integer.toString(payload.getClaimId()))
        .build();
  }

  private ExamOrder buildExamOrder(MasExamOrderStatusPayload payload) {
    return ExamOrder.builder()
        .collectionId(Integer.toString(payload.getCollectionId()))
        .status(payload.getCollectionStatus())
        .build();
  }
}
