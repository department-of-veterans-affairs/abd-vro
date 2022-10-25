package gov.va.vro.service.provider;

import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.service.MasCollectionAnnotsApiService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasPollingProcessor implements Processor {

  private final CamelEntrance camelEntrance;
  private final MasDelays masDelays;

  private final MasCollectionAnnotsApiService masCollectionAnnotsApiService;

  @Override
  @SneakyThrows
  public void process(Exchange exchange) {
    var payload = exchange.getMessage().getBody(MasAutomatedClaimPayload.class);
    log.info("Checking collection status for collection {}.", payload.getCollectionId());

    try {
      var result = masCollectionAnnotsApiService.getCollectionAnnots(payload.getCollectionId());
      log.info("RAJESH: " + result);
    } catch (MasException masException) {
      throw masException;
    }

    // call pcCheckCollectionStatus
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
  }

  private boolean checkCollectionStatus(int collectionId) {
    return new Random().nextBoolean();
  }
}
