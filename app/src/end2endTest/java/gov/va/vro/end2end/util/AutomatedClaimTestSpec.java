package gov.va.vro.end2end.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutomatedClaimTestSpec {
  private String collectionId;
  private String expectedMessage;
  private String payloadPath;
  private boolean checkSlack;
}
