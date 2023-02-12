package gov.va.vro.mockshared;

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
public class JwtSpecification extends DefaultClaims {
  private String secret;

  /**
   * Test Jwt properties that are used to generate Jwt for tests.
   *
   * @param props Test properties
   */
  public JwtSpecification(JwtAppConfig props) {
    secret = props.getSecret();
    setSubject(props.getSubject());
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
