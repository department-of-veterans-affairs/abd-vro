package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.api.ClaimsApi;
import gov.va.vro.mockbipclaims.model.bip.ClaimDetail;
import gov.va.vro.mockbipclaims.model.bip.response.ClaimDetailResponse;
import gov.va.vro.mockbipclaims.model.store.ClaimStore;
import gov.va.vro.mockbipclaims.model.store.ClaimStoreItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ClaimsController implements ClaimsApi {
  private final ClaimStore claimStore;

  @Override
  public ResponseEntity<ClaimDetailResponse> getClaimById(Long claimId) {
    log.info("Getting claim (id: {})", claimId);
    ClaimStoreItem item = claimStore.get(claimId);
    if (item == null) {
      String reason = "No claim found for id: " + claimId;
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
    }

    ClaimDetail claimDetail = item.getClaimDetail();

    ClaimDetailResponse response = new ClaimDetailResponse();
    response.setClaim(claimDetail);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
