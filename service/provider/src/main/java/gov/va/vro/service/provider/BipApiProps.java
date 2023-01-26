package gov.va.vro.service.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Properties used in BIP API services.
 *
 * @author warren @Date 10/31/22
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "bip")
public class BipApiProps {

  private String claimBaseUrl;

  private String claimSecret;

  private String claimClientId;

  private String claimIssuer;

  private String evidenceBaseUrl;

  private String evidenceSecret;

  private String evidenceClientId;

  private String evidenceIssuer;

  private String stationId;

  private String jti;

  private String applicationId;

  private String applicationName;

  /**
   * Creates common Jwt claims.
   *
   * @return Claims
   */
  public Claims toCommonJwtClaims() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, 30);
    Date expired = cal.getTime();
    Claims claims = Jwts.claims();
    claims.put("jti", jti);
    claims.put("applicationID", applicationId);
    claims.put("stationID", stationId);
    claims.put("userID", claimClientId);
    Date now = cal.getTime();
    claims.put("iat", now.getTime());
    claims.put("expires", expired.getTime());
    Map<String, Object> headerType = new HashMap<>();
    headerType.put("typ", Header.JWT_TYPE);

    return claims;
  }
}
