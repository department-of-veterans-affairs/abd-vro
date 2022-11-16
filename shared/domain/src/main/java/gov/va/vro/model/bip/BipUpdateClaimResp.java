package gov.va.vro.model.bip;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
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
