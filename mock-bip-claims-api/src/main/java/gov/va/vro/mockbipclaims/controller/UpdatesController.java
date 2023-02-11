package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.api.UpdatesApi;
import gov.va.vro.mockbipclaims.configuration.ClaimStore;
import gov.va.vro.mockbipclaims.configuration.ClaimStoreItem;
import gov.va.vro.mockbipclaims.model.store.ModifyingActionStore;
import gov.va.vro.mockbipclaims.model.store.ModifyingActionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class UpdatesController implements UpdatesApi {
  private final ClaimStore claimStore;

  private final ModifyingActionStore store;

  @Override
  public ResponseEntity<Void> deletedUpdated(Long claimId) {
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
  public ResponseEntity<ModifyingActionsResponse> getLifecycleStatusUpdated(Long claimId) {
    boolean found = store.isLifecycleStatusUpdated(claimId);
    ModifyingActionsResponse body = new ModifyingActionsResponse(found);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ModifyingActionsResponse> getContentionsUpdated(Long claimId) {
    boolean found = store.isContentionsUpdated(claimId);
    ModifyingActionsResponse body = new ModifyingActionsResponse(found);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }
}
