package gov.va.vro.mockbipclaims.api;

import gov.va.vro.mockbipclaims.configuration.ClaimStore;
import gov.va.vro.mockbipclaims.configuration.ClaimStoreItem;
import gov.va.vro.mockbipclaims.model.ClaimDetail;
import gov.va.vro.mockbipclaims.model.ClaimDetailResponse;
import gov.va.vro.mockbipclaims.model.ClaimLifecycleStatusesResponse;
import gov.va.vro.mockbipclaims.model.ContentionSummariesResponse;
import gov.va.vro.mockbipclaims.model.ContentionSummary;
import gov.va.vro.mockbipclaims.model.CreateContentionsRequest;
import gov.va.vro.mockbipclaims.model.CreateContentionsResponse;
import gov.va.vro.mockbipclaims.model.UpdateClaimLifecycleStatusRequest;
import gov.va.vro.mockbipclaims.model.UpdateClaimLifecycleStatusResponse;
import gov.va.vro.mockbipclaims.model.UpdateContentionsRequest;
import gov.va.vro.mockbipclaims.model.UpdateContentionsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ClaimsApiController implements ClaimsApi {
  @Autowired private ClaimStore claimStore;

  @Override
  public ResponseEntity<CreateContentionsResponse> createContentions(
      Long claimId, CreateContentionsRequest createContentionsRequest) {
    return null;
  }

  @Override
  public ResponseEntity<ContentionSummariesResponse> getContentionsForClaim(Long claimId) {
    ClaimStoreItem item = claimStore.get(claimId);
    List<ContentionSummary> contentions = item.getContentions();

    ContentionSummariesResponse response = new ContentionSummariesResponse();
    response.setContentions(contentions);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ClaimDetailResponse> getClaimById(Long claimId) {
    ClaimStoreItem item = claimStore.get(claimId);
    ClaimDetail claimDetail = item.getClaimDetail();

    ClaimDetailResponse response = new ClaimDetailResponse();
    response.setClaim(claimDetail);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ClaimLifecycleStatusesResponse> getClaimLifecycleStatuses(
      Long claimId, Boolean includeHistory) {
    return null;
  }

  @Override
  public ResponseEntity<UpdateClaimLifecycleStatusResponse> updateClaimLifecycleStatus(
      Long claimId, UpdateClaimLifecycleStatusRequest updateClaimLifecycleStatusRequest) {
    return null;
  }

  @Override
  public ResponseEntity<UpdateContentionsResponse> updateContentions(
      Long claimId, UpdateContentionsRequest updateContentionsRequest) {
    return null;
  }
}
