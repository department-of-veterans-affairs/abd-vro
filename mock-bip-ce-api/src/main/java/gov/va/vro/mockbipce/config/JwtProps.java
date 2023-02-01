package gov.va.vro.mockbipce.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "mock-bip-cce-api.jwt")
public class JwtProps {
  private String userId;
  private String secret;
  private String issuer;
  private String stationId;
  private int expirationSeconds;
}
