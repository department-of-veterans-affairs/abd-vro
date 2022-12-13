package gov.va.vro.model.bip;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class BipUpdateClaimResp {
  private final HttpStatus status;
  private final String message;
}
