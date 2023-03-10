package gov.va.vro.mockbipce.config;

import gov.va.vro.mockshared.jwt.JwtAppConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "mock-bip-ce-api.jwt")
public class JwtProps implements JwtAppConfig {
  @Override
  public String getSubject() {
    return "Evidence";
  }

  @Getter(onMethod = @__(@Override))
  private String userId;

  @Getter(onMethod = @__(@Override))
  private String secret;

  @Getter(onMethod = @__(@Override))
  private String issuer;

  @Getter(onMethod = @__(@Override))
  private String stationId;

  @Override
  public String getApplicationId() {
    return getIssuer();
  }

  @Getter(onMethod = @__(@Override))
  private int expirationSeconds;
}
