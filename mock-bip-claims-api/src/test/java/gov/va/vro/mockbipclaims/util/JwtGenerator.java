package gov.va.vro.mockbipclaims.util;

import gov.va.vro.mockbipclaims.config.JwtTestProps;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;

@Service
@RequiredArgsConstructor
public class JwtGenerator {
  private final JwtTestProps props;

  /**
   * Henerates the JWT.
   *
   * @return The JWT
   */
  public String generate() {
    JwtBuilder builder = addClaims();
    signJwt(builder);
    return builder.compact();
  }

  private void signJwt(JwtBuilder builder) {
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    String secret = props.getSecret();
    Key signingKey =
        new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), signatureAlgorithm.getJcaName());
    builder.signWith(signatureAlgorithm, signingKey);
  }

  private JwtBuilder addClaims() {
    Calendar currentTime = GregorianCalendar.getInstance();
    Date now = currentTime.getTime();

    Calendar expiration = GregorianCalendar.getInstance();
    expiration.setTime(now);
    expiration.add(Calendar.SECOND, props.getExpirationSeconds());

    return Jwts.builder()
        .setSubject("Claim")
        .setId(UUID.randomUUID().toString())
        .setHeaderParam("typ", "JWT")
        .setIssuedAt(now)
        .setExpiration(expiration.getTime())
        .setIssuer(props.getIssuer())
        // applicationID MUST be the same as the issuer for tracking purposes
        .claim("applicationID", props.getApplicationId())
        .claim("userID", props.getUserId())
        .claim("stationID", props.getStationId());
  }
}
