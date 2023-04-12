package gov.va.vro.model.bgs;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BgsApiClientResponse {
  public int statusCode;
  public String statusMessage;
}
