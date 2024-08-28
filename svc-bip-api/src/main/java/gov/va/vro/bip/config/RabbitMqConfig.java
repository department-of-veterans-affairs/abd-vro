package gov.va.vro.bip.config;

import gov.va.vro.bip.service.InvalidPayloadRejectingFatalExceptionStrategy;
import gov.va.vro.metricslogging.IMetricLoggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;

@Configuration
@Slf4j
@RequiredArgsConstructor
@ComponentScan("gov.va.vro.metricslogging")
public class RabbitMqConfig implements RabbitListenerConfigurer {

  private final RabbitMqConfigProperties props;
  private final JacksonConfig jacksonConfig;
  private final IMetricLoggerService metricLoggerService;

  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory());
    factory.setMessageConverter(
        (jacksonConfig.jackson2MessageConverter(jacksonConfig.objectMapper())));
    factory.setErrorHandler(
        new ConditionalRejectingErrorHandler(
            new InvalidPayloadRejectingFatalExceptionStrategy(metricLoggerService)));
    return factory;
  }

  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setHost(props.getHost());
    connectionFactory.setUsername(props.getUsername());
    connectionFactory.setPassword(props.getPassword());
    connectionFactory.setPort(props.getPort());
    return connectionFactory;
  }

  @Bean
  public DefaultMessageHandlerMethodFactory defaultHandlerMethodFactory() {
    DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
    factory.setValidator(amqpValidator());
    return factory;
  }

  @Bean
  public Validator amqpValidator() {
    return new OptionalValidatorFactoryBean();
  }

  @Override
  public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
    registrar.setContainerFactory(rabbitListenerContainerFactory());
    registrar.setMessageHandlerMethodFactory(defaultHandlerMethodFactory());
  }

  @Bean
  DirectExchange bipApiExchange() {
    return new DirectExchange(props.getExchangeName(), true, false);
  }

}
