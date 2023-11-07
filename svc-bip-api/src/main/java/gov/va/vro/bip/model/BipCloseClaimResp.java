package gov.va.vro.bip.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@JsonIgnoreProperties(ignoreUnknown = true) // BIP API can send messages
public class BipCloseClaimResp extends HasStatusCodeAndMessage {

  public int getStatus() {
    return statusCode;
  }

  public BipCloseClaimResp(HttpStatus status, String msg) {
    statusCode = status.value();
    statusMessage = msg;
  }
}
