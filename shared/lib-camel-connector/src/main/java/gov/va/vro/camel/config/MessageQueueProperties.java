package gov.va.vro.camel.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class MessageQueueProperties {
  private String host = "localhost";
  private int port = 5672;
  private String username = "guest";
  private String password = "guest";
}
