package gov.va.vro.bip.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static java.util.Map.entry;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitMqConfigProperties {
  @Value("${spring.rabbitmq.host}")
  private String host;

  @Value("${spring.rabbitmq.port}")
  private int port;

  @Value("${spring.rabbitmq.username}")
  private String username;

  @Value("${spring.rabbitmq.password}")
  private String password;

  @Value("${deadLetterQueueName}")
  private String deadLetterQueueName;

  @Value("${deadLetterExchangeName}")
  private String deadLetterExchangeName;

  Map<String, Object> getDeadLetterQueueArgs() {
    return Map.ofEntries(
            entry("x-dead-letter-exchange", ""), // empty string tells broker to use the default exchange
            entry("x-dead-letter-routing-key", deadLetterQueueName)
    );
  }
}
