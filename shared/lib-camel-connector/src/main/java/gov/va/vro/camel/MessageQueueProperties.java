package gov.va.vro.camel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "message-queue")
class MessageQueueProperties {
  private String host;
  private int port;
  private String username;
  // can we set defaults to use env var?
  // resources/conf-camel-rabbitmq.yml
  private String password;
}
