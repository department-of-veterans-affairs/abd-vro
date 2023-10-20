package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.api.ClaimsApi;
import gov.va.vro.mockbipclaims.model.bip.ClaimDetail;
import gov.va.vro.mockbipclaims.model.bip.Message;
import gov.va.vro.mockbipclaims.model.bip.request.CloseClaimRequest;
import gov.va.vro.mockbipclaims.model.bip.response.ClaimDetailResponse;
import gov.va.vro.mockbipclaims.model.bip.response.CloseClaimResponse;
import gov.va.vro.mockbipclaims.model.store.ClaimStore;
import gov.va.vro.mockbipclaims.model.store.ClaimStoreItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;

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
      return new ResponseEntity<>(createNotFoundResponse(), HttpStatus.NOT_FOUND);
    }

    ClaimDetail claimDetail = item.getClaimDetail();
    ClaimDetailResponse response = new ClaimDetailResponse();
    response.setClaim(claimDetail);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<CloseClaimResponse> cancelClaimById(
      Long claimId, CloseClaimRequest closeClaimRequest) {
    log.info("Canceling claim (id: {})", claimId);
    if (claimStore.get(claimId) == null) {
      return new ResponseEntity<>(createNotFoundCloseClaimResponse(), HttpStatus.NOT_FOUND);
    }

    claimStore.cancel(claimId);

    CloseClaimResponse response = new CloseClaimResponse();
    Message message = new Message();
    message.setText("Successfully canceled the claim with id: " + claimId);
    message.setStatus(HttpStatus.OK.value());
    response.addMessagesItem(message);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  private ClaimDetailResponse createNotFoundResponse() {
    ClaimDetailResponse response = new ClaimDetailResponse();
    response.addMessagesItem(createNotFoundMessage());
    return response;
  }

  private CloseClaimResponse createNotFoundCloseClaimResponse() {
    CloseClaimResponse response = new CloseClaimResponse();
    response.addMessagesItem(createNotFoundMessage());
    return response;
  }

  private Message createNotFoundMessage() {
    Message message = new Message();
    message.setText("Claim not found");
    message.setStatus(HttpStatus.NOT_FOUND.value());
    message.setSeverity("ERROR");
    message.setKey("bip.vetservices.claim.notfound");
    message.setTimestamp(OffsetDateTime.now());
    return message;
  }
}
