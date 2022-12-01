package gov.va.vro.service.provider.mas.service;

import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasExamOrderStatusPayload;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.MasConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MasProcessingService {

  private final CamelEntrance camelEntrance;

  private final MasConfig masConfig;

  public String processIncomingClaim(MasAutomatedClaimPayload payload) {

    if (payload.isInScope()) {
      // TODO: check if it has anchor
      camelEntrance.notifyAutomatedClaim(
          payload, masConfig.getMasProcessingInitialDelay(), masConfig.getMasRetryCount());
      return "Received";
    }
    // send slack notification
    var auditEvent = buildAuditEvent(payload);
    camelEntrance.sendSlack(auditEvent);
    camelEntrance.offRampClaim(payload);
    return "Out of scope";
  }

  public void examOrderingStatus(MasExamOrderStatusPayload payload) {
    camelEntrance.examOrderingStatus(payload);
  }

  private static AuditEvent buildAuditEvent(MasAutomatedClaimPayload payload) {
    return AuditEvent.builder()
        .eventId(Integer.toString(payload.getCollectionId()))
        .payloadType(MasAutomatedClaimPayload.class)
        .routeId("/automatedClaim")
        .message(
            String.format(
                "Request with [collection id = %s], [diagnostic code = %s], and [disability action type = %s] is not in scope.",
                payload.getCollectionId(),
                payload.getDiagnosticCode(),
                payload.getDisabilityActionType()))
        .build();
  }
}
