package gov.va.vro.mockmas.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "mock-mas.mas-api")
public class MasApiProperties {
  private String baseUrl;
  private String collectionStatusPath;
  private String collectionAnnotsPath;
  private String createExamOrderPath;
}
