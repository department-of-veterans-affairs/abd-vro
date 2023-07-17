package gov.va.vro.bip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class HasStatusCodeAndMessage {
  public int statusCode = 200;
  public String statusMessage = "OK";

  HasStatusCodeAndMessage() {}
}
