package gov.va.vro.mockbipclaims.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TestSpec {
  private long claimId;

  private int port;
  private boolean ignoreJwt = false;

  public String getUrl(String endPoint) {
    String baseUrl = "https://localhost:" + port;
    return baseUrl + endPoint;
  }
}
