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
import gov.va.vro.service.spi.model.ExamOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class MasProcessingService {

  private static final String customDateFormatRegex =
      "^(-?(?:[1-9][0-9]*)?[0-9]{4})-(1[0-2]|0[1-9])-(3[01]|0[1-9]|[12][0-9])(Z)?$";
  private static final Pattern customDatePattern = Pattern.compile(customDateFormatRegex);
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
    Claim claim = toClaim(payload);
    saveToDbService.insertClaim(claim);
    saveToDbService.insertFlashIds(payload.getVeteranFlashIds(), payload.getVeteranIcn());
    var offRampReasonOptional = getOffRampReason(payload);
    if (offRampReasonOptional.isPresent()) {
      var offRampReason = offRampReasonOptional.get();
      payload.setOffRampReason(offRampReason);
      claim.setOffRampReason(offRampReason);
      saveToDbService.setOffRampReason(claim);
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

  public void examOrderingStatus(MasExamOrderStatusPayload payload, String claimIdType) {
    saveToDbService.insertOrUpdateExamOrderingStatus(buildExamOrder(payload, claimIdType));
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
        .benefitClaimId(payload.getBenefitClaimId())
        .collectionId(Integer.toString(payload.getCollectionId()))
        .idType(payload.getIdType())
        .conditionName(payload.getConditionName())
        .diagnosticCode(payload.getDiagnosticCode())
        .veteranIcn(payload.getVeteranIcn())
        .veteranParticipantId(payload.getVeteranParticipantId())
        .inScope(payload.isInScope())
        .disabilityActionType(payload.getDisabilityActionType())
        .disabilityClassificationCode(payload.getDisabilityClassificationCode())
        .offRampReason(payload.getOffRampReason())
        .submissionSource(payload.getClaimDetail().getClaimSubmissionSource())
        .submissionDate(parseCustomDate(payload.getClaimDetail().getClaimSubmissionDateTime()))
        .build();
  }

  private OffsetDateTime parseCustomDate(String input) {
    OffsetDateTime customDateTime = null;
    try {
      if (input != null && !input.isBlank()) {
        // Attempt to parse non-standard ISO date we may be sent of YYYY-MM-DDZ
        Matcher customDateMatcher = customDatePattern.matcher(input);
        if (customDateMatcher.matches()) {
          Integer year = Integer.parseInt(customDateMatcher.group(0));
          Integer month = Integer.parseInt(customDateMatcher.group(1));
          Integer day = Integer.parseInt(customDateMatcher.group(2));
          LocalDate customDate = LocalDate.of(year, month, day);
          customDateTime = OffsetDateTime.of(customDate, LocalTime.MIN, ZoneOffset.UTC);
        } else {
          // Fall back to ISO 8601 Date Time
          customDateTime = OffsetDateTime.parse(input);
        }
      }
    } catch (Exception e) {
      log.error("Unable to parse date time. Unexpected date format {}", input);
    }
    return customDateTime;
  }

  private ExamOrder buildExamOrder(MasExamOrderStatusPayload payload, String claimIdType) {
    OffsetDateTime examDateTime = parseCustomDate(payload.getExamOrderDateTime());
    return ExamOrder.builder()
        .collectionId(Integer.toString(payload.getCollectionId()))
        .idType(claimIdType)
        .status(payload.getCollectionStatus())
        .examOrderDateTime(examDateTime)
        .build();
  }
}
