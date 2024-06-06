package gov.va.vro.bip.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.datadog")
public class DatadogConfigProperties {

  public static String site;
  public static String api_key;
  public static String app_key;
}
