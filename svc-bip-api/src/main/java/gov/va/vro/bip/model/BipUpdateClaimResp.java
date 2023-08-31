package gov.va.vro.bip.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper=false)
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
