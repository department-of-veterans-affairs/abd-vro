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
  //  private final CamelContext camelContext;
  //  private final CamelUtils camelUtils;

  private final MessageQueueProperties messageQueueProps;

  @Bean
  ConnectionFactory rabbitmqConnectionFactory() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(messageQueueProps.getHost());
    factory.setPort(messageQueueProps.getPort());
    factory.setUsername(messageQueueProps.getUser());
    factory.setPassword(messageQueueProps.getPassword());
    return factory;
  }
}
