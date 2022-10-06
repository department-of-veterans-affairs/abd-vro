package gov.va.vro.service.provider;

import gov.va.vro.model.mas.MasClaimDetailsPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MasPollingService {

  private final CamelEntrance camelEntrance;

  public String poll(MasClaimDetailsPayload payload) {
    // call pcCheckCollectionStatus
    boolean ready = checkCollectionStatus(payload.getCollectionsId());
    if (ready) {
      // call pcQueryCollectionAnnots
      // execute unspecified business logic
      // if a decision is made, call pcOrderExam
      return "processed automated claim request";
    } else {
      // re-request after some time
      camelEntrance.notifyAutomatedClaim(payload);
      return "re-queued notification";
    }
  }

  private boolean checkCollectionStatus(String collectionsId) {
    return new Random().nextBoolean();
  }
}
