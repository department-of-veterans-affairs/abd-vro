package gov.va.vro.mockbipclaims.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtTestProps {
  private String userId;
  private String secret;
  private String issuer;
  private String stationId;
  private int expirationSeconds;

  /**
   * Test Jwt properties that are use to generate Jwt for tests.
   *
   * @param props Test properties
   */
  public JwtTestProps(JwtProps props) {
    userId = props.getUserId();
    secret = props.getSecret();
    issuer = props.getIssuer();
    stationId = props.getStationId();
    expirationSeconds = props.getExpirationSeconds();
  }
}
