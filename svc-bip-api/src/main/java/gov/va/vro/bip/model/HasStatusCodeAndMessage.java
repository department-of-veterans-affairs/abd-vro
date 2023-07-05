package gov.va.vro.bip.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HasStatusCodeAndMessage {
  public int statusCode;
  public String statusMessage;

  HasStatusCodeAndMessage() {}
}
