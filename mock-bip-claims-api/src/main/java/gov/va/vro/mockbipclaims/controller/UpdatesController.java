package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.api.UpdatesApi;
import gov.va.vro.mockbipclaims.config.ClaimStore;
import gov.va.vro.mockbipclaims.config.ClaimStoreItem;
import gov.va.vro.mockbipclaims.model.store.UpdatesResponse;
import gov.va.vro.mockbipclaims.model.store.UpdatesStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class UpdatesController implements UpdatesApi {
  private final ClaimStore claimStore;

  private final UpdatesStore store;

  @Override
  public ResponseEntity<Void> deletedUpdates(Long claimId) {
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
  public ResponseEntity<UpdatesResponse> getLifecycleStatusUpdates(Long claimId) {
    boolean found = store.isLifecycleStatusUpdated(claimId);
    UpdatesResponse body = new UpdatesResponse(found);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<UpdatesResponse> getContentionsUpdates(Long claimId) {
    boolean found = store.isContentionsUpdated(claimId);
    UpdatesResponse body = new UpdatesResponse(found);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }
}
