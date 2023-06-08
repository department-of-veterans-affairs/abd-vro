package gov.va.vro.bip.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Data
public class BipUpdateClaimResp {
  private HttpStatus status;
  private String message;

  public BipUpdateClaimResp(HttpStatus status, String msg) {
    this.status = status;
    this.message = msg;
  }
}
