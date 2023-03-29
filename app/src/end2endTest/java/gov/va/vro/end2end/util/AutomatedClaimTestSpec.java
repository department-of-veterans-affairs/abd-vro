package gov.va.vro.end2end.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class AutomatedClaimTestSpec {
  private String collectionId;
  private String expectedMessage;

  private HttpStatus expectedStatusCode = HttpStatus.OK;
  private String payloadPath;
  private boolean bipError = false;
  private boolean masError = false;

  public AutomatedClaimTestSpec(String collectionId) {
    this.collectionId = collectionId;
  }
}
