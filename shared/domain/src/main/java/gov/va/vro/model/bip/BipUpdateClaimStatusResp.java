package gov.va.vro.model.bip;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class BipUpdateClaimStatusResp {
  private boolean isSuccessful;
  private String message;

  public BipUpdateClaimStatusResp(boolean success, String msg) {
    this.isSuccessful = success;
    this.message = msg;
  }
}
