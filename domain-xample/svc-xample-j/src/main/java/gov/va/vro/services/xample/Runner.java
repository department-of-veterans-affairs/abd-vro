package gov.va.vro.services.xample;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Runner implements CommandLineRunner {

  private final RabbitTemplate rabbitTemplate;
  private final Receiver receiver;

  public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
    this.receiver = receiver;
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void run(String... args) throws Exception {
    if (!true) {
      System.out.println("Sending message...");
      rabbitTemplate.convertAndSend(
          ServiceJApplication.exchangeName, ServiceJApplication.routingKey, "Hello from RabbitMQ!");
    }
    receiver.getLatch().await(1000000, TimeUnit.MILLISECONDS);
  }
}
