package gov.va.vro.bip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class HasStatusCodeAndMessage {
  @Builder.Default public int statusCode = 0;
  @Builder.Default public String statusMessage;

  HasStatusCodeAndMessage() {}
}
