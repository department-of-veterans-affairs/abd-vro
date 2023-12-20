package gov.va.vro.bip.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitMqConfigProperties {
  @Value("${spring.rabbitmq.host}")
  private String host = "localhost";

  @Value("${spring.rabbitmq.port}")
  private int port = 5672;

  @Value("${spring.rabbitmq.username}")
  private String username = "user";

  @Value("${spring.rabbitmq.password}")
  private String password = "bitnami";
}
