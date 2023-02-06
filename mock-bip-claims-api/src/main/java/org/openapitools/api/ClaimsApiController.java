package org.openapitools.api;

import org.openapitools.configuration.ClaimStore;
import org.openapitools.configuration.ClaimStoreItem;
import org.openapitools.model.ClaimDetail;
import org.openapitools.model.ClaimDetailResponse;
import org.openapitools.model.ClaimLifecycleStatusesResponse;
import org.openapitools.model.ContentionSummariesResponse;
import org.openapitools.model.ContentionSummary;
import org.openapitools.model.CreateContentionsRequest;
import org.openapitools.model.CreateContentionsResponse;
import org.openapitools.model.UpdateClaimLifecycleStatusRequest;
import org.openapitools.model.UpdateClaimLifecycleStatusResponse;
import org.openapitools.model.UpdateContentionsRequest;
import org.openapitools.model.UpdateContentionsResponse;
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
