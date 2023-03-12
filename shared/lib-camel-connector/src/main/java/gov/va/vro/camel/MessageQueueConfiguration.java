package gov.va.vro.camel;

import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MessageQueueConfiguration {
  private final MessageQueueProperties messageQueueProps;

  @Bean
  ConnectionFactory rabbitmqConnectionFactory() {
    log.info("rabbitmq ConnectionFactory: connecting to {}", messageQueueProps.getHost());
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(messageQueueProps.getHost());
    factory.setPort(messageQueueProps.getPort());
    factory.setUsername(messageQueueProps.getUsername());
    factory.setPassword(messageQueueProps.getPassword());
    return factory;
  }
}
