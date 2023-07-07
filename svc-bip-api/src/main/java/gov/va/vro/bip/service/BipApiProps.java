package gov.va.vro.bip.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Calendar;
import java.util.Date;

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

  private String applicationId;

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
    claims.put("applicationID", applicationId);
    claims.put("stationID", stationId);
    claims.put("userID", claimClientId);
    Date now = cal.getTime();
    claims.put("iat", now.getTime());
    claims.put("expires", expired.getTime());
    return claims;
  }
}
