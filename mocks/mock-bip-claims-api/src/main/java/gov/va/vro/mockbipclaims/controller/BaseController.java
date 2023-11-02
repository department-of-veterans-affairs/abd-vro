package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.model.bip.Message;
import gov.va.vro.mockbipclaims.model.bip.ProviderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

public class BaseController {

  protected <T extends ProviderResponse> ResponseEntity<T> create200(T response) {
    return new ResponseEntity<T>(response, HttpStatus.OK);
  }

  protected <T extends ProviderResponse> ResponseEntity<T> create500(T response) {
    response.addMessagesItem(createInternalServerMessage());
    return new ResponseEntity<T>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  protected <T extends ProviderResponse> ResponseEntity<T> create404(T response) {
    response.addMessagesItem(createNotFoundMessage());
    return new ResponseEntity<T>(response, HttpStatus.NOT_FOUND);
  }

  public static Message createNotFoundMessage() {
    Message message = new Message();
    message.setText("Claim not found");
    message.setStatus(HttpStatus.NOT_FOUND.value());
    message.setSeverity("ERROR");
    message.setKey("bip.vetservices.claim.notfound");
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
