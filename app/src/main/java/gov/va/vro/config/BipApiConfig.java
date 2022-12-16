package gov.va.vro.config;

import gov.va.vro.service.provider.BipApiProps;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures BIP API access.
 *
 * @author warren @Date 10/31/22
 */
@Configuration
public class BipApiConfig {
  @Bean
  @ConfigurationProperties(prefix = "bip")
  public BipApiProps getBipApiProps() {
    return new BipApiProps();
  }
}
