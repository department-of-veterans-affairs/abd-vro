package gov.va.vro.abddataaccess.config;

import gov.va.vro.abddataaccess.config.properties.LighthouseProperties;
import gov.va.vro.abddataaccess.config.properties.RabbitMqProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProperties {
  @Bean
  @ConfigurationProperties(prefix = "abd-data-access.rabbitmq")
  public RabbitMqProperties rabbitmq() {
    return new RabbitMqProperties();
  }

  @Bean
  public LighthouseProperties lighthouseProperties() {
    return new LighthouseProperties();
  }
}
