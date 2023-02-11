package gov.va.vro.mockbipclaims.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "mock-bip-claims-api.jwt")
public class JwtProps {
  private String userId;
  private String secret;
  private String issuer;
  private String stationId;
  private String applicationId;
  private int expirationSeconds;
}
