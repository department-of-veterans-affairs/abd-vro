package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.api.LifecycleStatusesApi;
import gov.va.vro.mockbipclaims.model.bip.Message;
import gov.va.vro.mockbipclaims.model.bip.request.UpdateClaimLifecycleStatusRequest;
import gov.va.vro.mockbipclaims.model.bip.response.UpdateClaimLifecycleStatusResponse;
import gov.va.vro.mockbipclaims.model.store.ClaimStore;
import gov.va.vro.mockbipclaims.model.store.ClaimStoreItem;
import gov.va.vro.mockbipclaims.model.store.UpdatesStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LifecycleStatusesController implements LifecycleStatusesApi {
  private final ClaimStore claimStore;

  private final UpdatesStore actionStore;

  @Override
  public ResponseEntity<UpdateClaimLifecycleStatusResponse> updateClaimLifecycleStatus(
      Long claimId, UpdateClaimLifecycleStatusRequest updateClaimLifecycleStatusRequest) {
    log.info("Updating claim lifecycle status (id: {})", claimId);
    ClaimStoreItem item = claimStore.get(claimId);
    if (item == null) {
      String reason = "No claim found for id: " + claimId;
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
    }

    if (claimId.longValue() == 1370L) {
      String reason = "Intentional exception for testing: " + claimId;
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason);
    }

    String status = updateClaimLifecycleStatusRequest.getClaimLifecycleStatus();
    item.getClaimDetail().setClaimLifecycleStatus(status);
    var response = new UpdateClaimLifecycleStatusResponse();
    Message message = new Message();
    message.setText("Success");
    response.addMessagesItem(message);

    actionStore.addLifecycleStatusUpdate(claimId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
