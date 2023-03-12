package gov.va.vro.services.xample;

// https://spring.io/guides/gs/messaging-rabbitmq/

// import org.springdoc.hateoas.SpringDocHateoasConfiguration;

import org.springdoc.hateoas.SpringDocHateoasConfiguration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {SpringDocHateoasConfiguration.class})
public class ServiceJApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServiceJApplication.class, args).close();
  }

  // @Bean
  // MessageListenerAdapter listenerAdapter(Receiver receiver) {
  //   return new MessageListenerAdapter(receiver, "receiveMessage");
  // }

  static final String exchangeName = "xample";

  static final String queueName = "serviceJ";

  static final String routingKey = "serviceJ";

  // Spring AMQP requires that the Queue, the DirectExchange/TopicExchange, and the Binding be
  // declared as top-level Spring beans in order to be set up properly.

  @Bean
  Queue queue() {
    return new Queue(queueName, true, false, true);
  }

  @Bean
  DirectExchange exchange() {
    return new DirectExchange(exchangeName, true, true);
  }

  @Bean
  Binding binding(Queue queue, DirectExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(routingKey);
  }

  @Bean
  public MessageConverter jackson2MessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  // @Bean
  // SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
  //                                          MessageListenerAdapter listenerAdapter) {
  //   SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
  //   container.setConnectionFactory(connectionFactory);
  //   container.setQueueNames(queueName);
  //   container.setMessageListener(listenerAdapter);
  //   return container;
  // }

}
