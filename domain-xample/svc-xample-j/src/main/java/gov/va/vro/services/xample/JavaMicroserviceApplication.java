package gov.va.vro.services.xample;

import gov.va.vro.model.xample.SomeDtoModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@Slf4j
public class JavaMicroserviceApplication {
  // Also see https://spring.io/guides/gs/messaging-rabbitmq/

  @Bean
  public CountDownLatch shutdownLatch() {
    return new CountDownLatch(1);
  }

  public static void main(String[] args) throws InterruptedException {
    var ctx = SpringApplication.run(JavaMicroserviceApplication.class, args);

    // Keep this application running
    final CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread() {
              @Override
              public void run() {
                closeLatch.countDown();
              }
            });
    closeLatch.await();
  }

  static final String exchangeName = "xample";

  static final String queueName = "serviceJ";

  static final String routingKey = "serviceJ";

  // Spring AMQP requires that the Queue, the DirectExchange/TopicExchange, and the Binding be
  // declared as top-level Spring beans in order to be set up properly.

  @Bean
  Queue queue1() {
    return new Queue(queueName, true, false, true);
  }

  @Bean
  DirectExchange exchange1() {
    return new DirectExchange(exchangeName, true, true);
  }

  @Bean
  Binding binding1() {
    return BindingBuilder.bind(queue1()).to(exchange1()).with(routingKey);
  }

  @Bean
  public MessageConverter jackson2MessageConverter() {
    var converter = new Jackson2JsonMessageConverter();
    // converter.setAlwaysConvertToInferredType(true);
    return converter;
  }

  @Bean
  RabbitListenerErrorHandler xampleErrorHandler() {
    RabbitListenerErrorHandler handler =
        new RabbitListenerErrorHandler() {
          @Override
          public Object handleError(
              Message amqpMessage,
              org.springframework.messaging.Message<?> message,
              ListenerExecutionFailedException exception) {
            try {
              log.error("Oh no!", exception);
              var errorModel = SomeDtoModel.builder().resourceId("").diagnosticCode("").build();
              errorModel.header(500, exception.toString());
              return errorModel;
            }
            catch(Exception e){
              log.error("Exception handler threw exception. Call 911. Exiting");
              System.exit(-1);
              return null; //never executed
            }
          }
        };
    return handler;
  }
}
