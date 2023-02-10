package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.api.ModifyingActionsApi;
import gov.va.vro.mockbipclaims.model.store.ModifyingActionStore;
import gov.va.vro.mockbipclaims.model.store.ModifyingActionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ModifyingActionsController implements ModifyingActionsApi {
  private final ModifyingActionStore store;

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
