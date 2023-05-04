package gov.va.vro.model.rrd.bgs;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BgsApiClientResponse {
  public int statusCode;
  public String statusMessage;
}
