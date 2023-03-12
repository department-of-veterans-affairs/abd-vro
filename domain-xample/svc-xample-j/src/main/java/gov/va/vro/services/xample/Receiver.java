package gov.va.vro.services.xample;

import gov.va.vro.model.xample.SomeDtoModel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class Receiver {

  private CountDownLatch latch = new CountDownLatch(1);

  @RabbitListener(queues = "#{queue.name}")
  public SomeDtoModel receiveMessage(SomeDtoModel message) {
    System.out.println("Received <" + message + ">");
    // latch.countDown();
    return message;
  }

  // public String receiveMessage(byte[] message) {
  //   System.out.println("Received <" + message + ">");
  //   System.out.println("Received 2 <" + new String(message) + ">");
  //   //latch.countDown();
  //   return new String(message);
  // }

  public CountDownLatch getLatch() {
    return latch;
  }
}
