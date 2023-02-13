package gov.va.vro.mockbipclaims.config;

import gov.va.vro.mockshared.jwt.JwtAppConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@ConfigurationProperties(prefix = "mock-bip-claims-api.jwt")
public class JwtProps implements JwtAppConfig {
  @Override
  public String getSubject() {
    return "Claim";
  }

  @Getter(onMethod = @__(@Override))
  private String userId;

  @Getter(onMethod = @__(@Override))
  private String secret;

  @Getter(onMethod = @__(@Override))
  private String issuer;

  @Getter(onMethod = @__(@Override))
  private String stationId;

  @Getter(onMethod = @__(@Override))
  private String applicationId;

  @Getter(onMethod = @__(@Override))
  private int expirationSeconds;
}
