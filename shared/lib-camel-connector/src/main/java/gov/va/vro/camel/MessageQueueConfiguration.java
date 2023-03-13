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
  private final MessageQueueProperties mqProperties;
  private final MessageQueueEnvVariables mqEnvVariables;

  @Bean
  ConnectionFactory rabbitmqConnectionFactory() {
    ConnectionFactory factory = new ConnectionFactory();

    // Prefer environment variables over properties in application.yml
    factory.setHost(mqEnvVariables.getHost());
    factory.setPort(mqEnvVariables.getPort());
    factory.setUsername(mqEnvVariables.getUsername());
    factory.setPassword(mqEnvVariables.getPassword());

    if (factory.getHost() == null) factory.setHost(mqProperties.getHost());
    if (factory.getPort() == 0) factory.setPort(mqProperties.getPort());
    if (factory.getUsername() == null) factory.setUsername(mqProperties.getUsername());
    if (factory.getPassword() == null) factory.setPassword(mqProperties.getPassword());

    log.info(
        "rabbitmq ConnectionFactory: connecting to {}:{}", factory.getHost(), factory.getPort());
    return factory;
  }
}
