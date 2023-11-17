package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.model.bip.Message;
import gov.va.vro.mockbipclaims.model.bip.ProviderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

public class BaseController {
  protected static final int CLAIM_YIELDS_500 = 500;

  protected <T extends ProviderResponse> ResponseEntity<T> create200(T response) {
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  protected <T extends ProviderResponse> ResponseEntity<T> create500(T response) {
    response.addMessagesItem(createInternalServerMessage());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  protected <T extends ProviderResponse> ResponseEntity<T> createClaim404(
      T response, long claimId) {
    response.addMessagesItem(createClaimNotFoundMessage(claimId, HttpStatus.NOT_FOUND));
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  protected <T extends ProviderResponse> ResponseEntity<T> createClaim400(
      T response, long claimId) {
    response.addMessagesItem(createClaimNotFoundMessage(claimId, HttpStatus.BAD_REQUEST));
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  protected <T extends ProviderResponse> ResponseEntity<T> createContention400(
      T response, long claimId, long contentionId) {
    response.addMessagesItem(createContentionNotAssociatedToClaimMessage(claimId, contentionId));
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  public static Message createClaimNotFoundMessage(long claimId, HttpStatus status) {
    Message message = new Message();
    message.setText("Claim ID " + claimId + " not found");
    message.setStatus(status.value());
    message.setSeverity("ERROR");
    message.setKey("bip.vetservices.claim.notfound");
    message.setTimestamp(OffsetDateTime.now());
    return message;
  }

  public static Message createContentionNotAssociatedToClaimMessage(
      long claimId, long contentionId) {
    Message message = new Message();
    message.setText("contentionId " + contentionId + " is not associated to claimId " + claimId);
    message.setStatus(HttpStatus.NOT_FOUND.value());
    message.setSeverity("ERROR");
    message.setKey("bip.vetservices.conention.notfound");
    message.setTimestamp(OffsetDateTime.now());
    return message;
  }

  public static Message createInternalServerMessage() {
    Message message = new Message();
    message.setText(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    message.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    message.setSeverity("FATAL");
    message.setKey("bip.vetservices.claim.internalservererror");
    message.setTimestamp(OffsetDateTime.now());
    return message;
  }
}
