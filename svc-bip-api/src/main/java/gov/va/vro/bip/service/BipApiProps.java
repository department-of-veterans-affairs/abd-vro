package gov.va.vro.bip.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
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
    ClaimsBuilder claimsBuilder = Jwts.claims()
            .add("applicationID", applicationId)
            .add("stationID", stationId)
            .add("userID", claimClientId);

    Date now = cal.getTime();
    claimsBuilder.add("iat", now.getTime())
            .add("expires", expired.getTime());

    return claimsBuilder.build();
  }
}
