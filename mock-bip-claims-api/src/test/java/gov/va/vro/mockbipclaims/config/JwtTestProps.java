package gov.va.vro.mockbipclaims.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.crypto.spec.SecretKeySpec;

@Getter
@Setter
public class JwtTestProps extends DefaultClaims {
  private String secret;

  /**
   * Test Jwt properties that are used to generate Jwt for tests.
   *
   * @param props Test properties
   */
  public JwtTestProps(JwtProps props) {
    secret = props.getSecret();
    setSubject("Claim");
    setIssuer(props.getIssuer());
    put("applicationID", props.getApplicationId());
    put("userID", props.getUserId());
    put("stationID", props.getStationId());

    Calendar currentTime = GregorianCalendar.getInstance();
    Date now = currentTime.getTime();
    setIssuedAt(now);

    Calendar expiration = GregorianCalendar.getInstance();
    expiration.setTime(now);
    expiration.add(Calendar.SECOND, props.getExpirationSeconds());
    setExpiration(expiration.getTime());
  }

  public SignatureAlgorithm getSignatureAlgorithm() {
    return SignatureAlgorithm.HS256;
  }

  public Key getSigningKey() {
    SignatureAlgorithm signatureAlgorithm = getSignatureAlgorithm();
    byte[] secretBytes = getSecret().getBytes(StandardCharsets.UTF_8);
    return new SecretKeySpec(secretBytes, signatureAlgorithm.getJcaName());
  }
}
