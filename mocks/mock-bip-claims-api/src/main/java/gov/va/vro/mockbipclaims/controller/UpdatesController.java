package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.api.UpdatesApi;
import gov.va.vro.mockbipclaims.model.bip.ContentionSummary;
import gov.va.vro.mockbipclaims.model.mock.response.UpdatesResponse;
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
    ClaimStoreItem item = claimStore.get(claimId);
    boolean found = (item != null && store.isLifecycleStatusUpdated(claimId));
    UpdatesResponse body = new UpdatesResponse(found);
    if (found) {
      String status = item.getClaimDetail().getClaimLifecycleStatus();
      body.setStatus(status);
    }
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<UpdatesResponse> getContentionsUpdates(Long claimId) {
    ClaimStoreItem item = claimStore.get(claimId);
    boolean found = (item != null && store.isContentionsUpdated(claimId));
    UpdatesResponse body = new UpdatesResponse(found);
    if (found) {
      List<ContentionSummary> contentions = item.getContentions();
      body.setContentions(contentions);
    }
    return new ResponseEntity<>(body, HttpStatus.OK);
  }
}
