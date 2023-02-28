package gov.va.vro.mocklh.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "mock-lh.lh-api")
public class LhApiProperties {
  private String tokenUrl;
  private String fhirUrl;
  private String assertionUrl;
  private String pemKey;
  private String clientId;
}
