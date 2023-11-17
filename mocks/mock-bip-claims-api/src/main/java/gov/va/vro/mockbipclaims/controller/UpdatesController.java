package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.api.UpdatesApi;
import gov.va.vro.mockbipclaims.model.bip.ContentionSummary;
import gov.va.vro.mockbipclaims.model.mock.response.ContentionUpdatesResponse;
import gov.va.vro.mockbipclaims.model.mock.response.LifecycleUpdatesResponse;
import gov.va.vro.mockbipclaims.model.store.ClaimStore;
import gov.va.vro.mockbipclaims.model.store.ClaimStoreItem;
import gov.va.vro.mockbipclaims.model.store.UpdatesStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UpdatesController implements UpdatesApi {
  private final ClaimStore claimStore;

  private final UpdatesStore store;

  @Override
  public ResponseEntity<Void> deleteUpdates(Long claimId) {
    ClaimStoreItem item = claimStore.get(claimId);
    if (item == null) {
      String reason = "No claim found for id: " + claimId;
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
    }
    store.reset(claimId);
    item.reset();
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Override
  public ResponseEntity<LifecycleUpdatesResponse> getLifecycleStatusUpdates(Long claimId) {
    ClaimStoreItem item = claimStore.get(claimId);
    if (item == null) {
      String reason = "No claim found for id: " + claimId;
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
    }
    boolean found = store.isLifecycleStatusUpdated(claimId);
    LifecycleUpdatesResponse body = new LifecycleUpdatesResponse(found);
    String status = item.getClaimDetail().getClaimLifecycleStatus();
    body.setStatus(status);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ContentionUpdatesResponse> getContentionsUpdates(Long claimId) {
    ClaimStoreItem item = claimStore.get(claimId);
    boolean found = (item != null && store.isContentionsUpdated(claimId));
    ContentionUpdatesResponse body = new ContentionUpdatesResponse(found);
    if (found) {
      List<ContentionSummary> contentions = item.getContentions();
      body.setContentions(contentions);
    }
    return new ResponseEntity<>(body, HttpStatus.OK);
  }
}
