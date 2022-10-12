package gov.va.vro.service.provider;

import gov.va.vro.model.mas.MasClaimDetailsPayload;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.model.MasCollectionStatus;
import gov.va.vro.service.provider.mas.model.MasStatus;
import gov.va.vro.service.provider.mas.service.MasApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasPollingProcessor implements Processor {

  private final CamelEntrance camelEntrance;
  private final MasDelays masDelays;
  private final MasApiService masService;

  @Override
  public void process(Exchange exchange) {
    var payload = exchange.getMessage().getBody(MasClaimDetailsPayload.class);
    log.info("Checking collection status for collection {}.", payload.getCollectionId());
    // call pcCheckCollectionStatus
    try {
      boolean ready = checkCollectionStatus(payload.getCollectionId());
      if (ready) {
        log.info("Collection {} is ready for processing.", payload.getCollectionId());
        // call pcQueryCollectionAnnots
        // call Lighthouse
        // Combine results and call PDF generation
        // if a decision is made, call pcOrderExam
      } else {
        log.info("Collection {} is not ready. Requeueing...", payload.getCollectionId());
        // re-request after some time
        camelEntrance.notifyAutomatedClaim(payload, masDelays.getMasProcessingSubsequentDelay());
      }
    } catch (MasException e) {
      log.error("MAS collection {} status check failed.", payload.getCollectionId(), e);
    }
  }

  private boolean checkCollectionStatus(String collectionsId) throws MasException {
    // return new Random().nextBoolean();
    try {
      int masCollectionId = Integer.parseInt(collectionsId);
      List<MasCollectionStatus> statusList =
          masService.getMasCollectionStatus(Collections.singletonList(masCollectionId));
      if (!statusList.isEmpty()) {
        MasStatus status = MasStatus.valueOf(statusList.get(0).getCollectionStatus());
        switch (status) {
          case PROCESSED:
            return true;
          default:
            return false;
        }
      }
      return false;
    } catch (NumberFormatException e) {
      throw new MasException("Invalid collection Id " + collectionsId, e);
    }
  }
}
