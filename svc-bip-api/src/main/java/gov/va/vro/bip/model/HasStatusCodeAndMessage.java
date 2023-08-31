package gov.va.vro.bip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class HasStatusCodeAndMessage {
  @Builder.Default
  public int statusCode = 200;
  @Builder.Default
  public String statusMessage = "OK";

  HasStatusCodeAndMessage() {}
}
