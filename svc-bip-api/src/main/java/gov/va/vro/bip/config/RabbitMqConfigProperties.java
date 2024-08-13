package gov.va.vro.bip.config;

import static java.util.Map.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitMqConfigProperties {
  @Value("${spring.rabbitmq.host:localhost}")
  private String host;

  @Value("${spring.rabbitmq.port:5672}")
  private int port;

  @Value("${spring.rabbitmq.username:user}")
  private String username;

  @Value("${spring.rabbitmq.password:bitnami}")
  private String password;

  @Value("${deadLetterQueueName:vroDeadLetterQueue}")
  private String deadLetterQueueName;

  @Value("${deadLetterExchangeName:vro.dlx}")
  private String deadLetterExchangeName;

  Map<String, Object> getDeadLetterQueueArgs() {
    return Map.ofEntries(entry("x-dead-letter-exchange", deadLetterExchangeName));
  }
}
