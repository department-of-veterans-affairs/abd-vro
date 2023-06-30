package gov.va.vro.bip.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Data
public class BipUpdateClaimResp extends HasStatusCodeAndMessage {

  public int getStatus() {
    return statusCode;
  }

  public BipUpdateClaimResp(HttpStatus status, String msg) {
    statusCode = status.value();
    statusMessage = msg;
  }
}
