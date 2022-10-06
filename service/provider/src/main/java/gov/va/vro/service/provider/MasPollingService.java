package gov.va.vro.service.provider;

import gov.va.vro.model.mas.MasClaimDetailsPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class MasPollingService {

  private final CamelEntrance camelEntrance;

  public String poll(MasClaimDetailsPayload payload) {
    log.info("Checking collection status for collection {}.", payload.getCollectionsId());
    // call pcCheckCollectionStatus
    boolean ready = checkCollectionStatus(payload.getCollectionsId());
    if (ready) {
      log.info("Collection {} is ready for processing.", payload.getCollectionsId());
      // call pcQueryCollectionAnnots
      // execute unspecified business logic
      // if a decision is made, call pcOrderExam
      return "processed automated claim request";
    } else {
      log.info("Collection {} is not ready. Requeueing...", payload.getCollectionsId());
      // re-request after some time
      // TODO: figure out async
      new Thread(() -> camelEntrance.notifyAutomatedClaim(payload)).start();
    }
    return "not ready";
  }

  private boolean checkCollectionStatus(String collectionsId) {
    return new Random().nextBoolean();
  }
}
