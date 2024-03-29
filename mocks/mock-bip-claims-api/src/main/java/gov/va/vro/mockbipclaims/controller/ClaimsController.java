package gov.va.vro.mockbipclaims.controller;

import static gov.va.vro.mockbipclaims.config.ClaimIdConstants.CLAIM_ID_ALL_ENDPOINTS_YIELDS_500;
import static gov.va.vro.mockbipclaims.config.ClaimIdConstants.CLAIM_ID_CANCEL_CLAIM_YIELDS_500;
import static gov.va.vro.mockbipclaims.config.ClaimIdConstants.CLAIM_ID_GET_CLAIM_DETAILS_YIELDS_500;
import static gov.va.vro.mockbipclaims.config.ClaimIdConstants.CLAIM_ID_SET_TSOJ_YIELDS_500;

import gov.va.vro.mockbipclaims.api.ClaimsApi;
import gov.va.vro.mockbipclaims.model.bip.request.CloseClaimRequest;
import gov.va.vro.mockbipclaims.model.bip.request.PutTemporaryStationOfJurisdictionRequest;
import gov.va.vro.mockbipclaims.model.bip.response.ClaimDetailResponse;
import gov.va.vro.mockbipclaims.model.bip.response.CloseClaimResponse;
import gov.va.vro.mockbipclaims.model.bip.response.PutTemporaryStationOfJurisdictionResponse;
import gov.va.vro.mockbipclaims.model.store.ClaimStore;
import gov.va.vro.mockbipclaims.model.store.ClaimStoreItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ClaimsController extends BaseController implements ClaimsApi {
  private final ClaimStore claimStore;

  @Override
  public ResponseEntity<ClaimDetailResponse> getClaimById(Long claimId) {
    log.info("Getting claim (id: {})", claimId);

    ClaimStoreItem item = claimStore.get(claimId);

    ClaimDetailResponse response = new ClaimDetailResponse();
    if (item == null) {
      return createClaim404(response, claimId);
    }
    if (claimId == CLAIM_ID_ALL_ENDPOINTS_YIELDS_500
        || claimId == CLAIM_ID_GET_CLAIM_DETAILS_YIELDS_500) {
      return create500(response);
    }

    response.setClaim(item.getClaimDetail());

    return create200(response);
  }

  @Override
  public ResponseEntity<CloseClaimResponse> cancelClaimById(
      Long claimId, CloseClaimRequest closeClaimRequest) {
    log.info("Canceling claim (id: {})", claimId);

    ClaimStoreItem item = claimStore.get(claimId);

    CloseClaimResponse response = new CloseClaimResponse();
    if (item == null) {
      return createClaim404(response, claimId);
    }

    if (claimId == CLAIM_ID_ALL_ENDPOINTS_YIELDS_500
        || claimId == CLAIM_ID_CANCEL_CLAIM_YIELDS_500) {
      return create500(response);
    }

    if (closeClaimRequest != null) {
      log.info(
          "Received lifecycleStatusReasonCode: {}",
          closeClaimRequest.getLifecycleStatusReasonCode());
      log.info("Received closeReasonText: {}", closeClaimRequest.getCloseReasonText());
    }

    item.getClaimDetail().setClaimLifecycleStatus("Cancelled");

    return create200(response);
  }

  @Override
  public ResponseEntity<PutTemporaryStationOfJurisdictionResponse>
      putTemporaryStationOfJurisdictionById(
          Long claimId, PutTemporaryStationOfJurisdictionRequest request) {

    log.info("Updating temporary station of jurisdiction (id: {})", claimId);

    ClaimStoreItem item = claimStore.get(claimId);

    var response = new PutTemporaryStationOfJurisdictionResponse();
    if (item == null) {
      return createClaim404(response, claimId);
    }

    if (claimId == CLAIM_ID_ALL_ENDPOINTS_YIELDS_500 || claimId == CLAIM_ID_SET_TSOJ_YIELDS_500) {
      return create500(response);
    }

    item.getClaimDetail().setTempStationOfJurisdiction(request.getTempStationOfJurisdiction());

    return create200(response);
  }
}
