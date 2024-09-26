package gov.va.vro.bip.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Properties used in BIP API services.
 *
 * @author warren @Date 10/31/22
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "bip")
@NoArgsConstructor
public class BipApiProps {

  static final String HTTPS = "https://";

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

  public String getClaimRequestUrl(String path) {
    String baseUrl = this.claimBaseUrl;
    if (!baseUrl.startsWith("http")) {
      baseUrl = HTTPS + baseUrl;
    }
    return baseUrl + path;
  }

  public String getAvailabilityUrl() {
    return getClaimRequestUrl(BipApiService.SPECIAL_ISSUE_TYPES);
  }

  /**
   * Creates common Jwt claims.
   *
   * @return Claims
   */
  public Claims toCommonJwtClaims(final String externalUserId, final String externalKey) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, 30);
    Date expired = cal.getTime();
    ClaimsBuilder claimsBuilder =
        Jwts.claims()
            .add("applicationID", applicationId)
            .add("stationID", stationId)
            .add("userID", claimClientId);

    if (Objects.nonNull(externalUserId)) {
      claimsBuilder.add("externalUserId", externalUserId);
    }
    if (Objects.nonNull(externalKey)) {
      claimsBuilder.add("externalKey", externalKey);
    }

    Date now = cal.getTime();
    claimsBuilder.add("iat", now.getTime()).add("expires", expired.getTime());

    return claimsBuilder.build();
  }
}
