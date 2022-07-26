package gov.va.vro.abd_data_access.config;

import gov.va.vro.abd_data_access.config.properties.LighthouseProperties;
import gov.va.vro.abd_data_access.config.properties.RabbitMQProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProperties {
  @Bean
  @ConfigurationProperties(prefix = "abd-data-access.rabbitmq")
  public RabbitMQProperties rabbitmq() {
    return new RabbitMQProperties();
  }

  @Bean
  @ConfigurationProperties(prefix = "lh")
  public LighthouseProperties lighthouseProperties() {
    return new LighthouseProperties();
  }
}
