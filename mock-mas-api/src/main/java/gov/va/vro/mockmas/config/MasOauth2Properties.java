package gov.va.vro.mockmas.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "mock-mas.mas-oauth2")
public class MasOauth2Properties {
  private String tokenUri;
  private String clientId;
  private String clientSecret;
  private String scope;
  private String grantType;
}
