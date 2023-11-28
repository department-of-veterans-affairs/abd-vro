package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.api.LifecycleStatusesApi;
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

@Controller
@Slf4j
@RequiredArgsConstructor
public class LifecycleStatusesController extends BaseController implements LifecycleStatusesApi {
  private final ClaimStore claimStore;

  private final UpdatesStore actionStore;

  @Override
  public ResponseEntity<UpdateClaimLifecycleStatusResponse> updateClaimLifecycleStatus(
      Long claimId, UpdateClaimLifecycleStatusRequest updateClaimLifecycleStatusRequest) {
    log.info("Updating claim lifecycle status (id: {})", claimId);
    ClaimStoreItem item = claimStore.get(claimId);

    UpdateClaimLifecycleStatusResponse response = new UpdateClaimLifecycleStatusResponse();
    if (item == null) {
      return createClaim404(response, claimId);
    }
    if (claimId == CLAIM_YIELDS_500) {
      return create500(response);
    }

    String status = updateClaimLifecycleStatusRequest.getClaimLifecycleStatus();
    item.getClaimDetail().setClaimLifecycleStatus(status);

    actionStore.addLifecycleStatusUpdate(claimId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
