package gov.va.vro.config;

import gov.va.vro.config.propmodel.OpenApi;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "vro")
public class VroProperties {
  private final OpenApi openApi = new OpenApi();
}
