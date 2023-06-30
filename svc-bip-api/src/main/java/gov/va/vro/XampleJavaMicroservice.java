package gov.va.vro;

import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.model.xample.StatusValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class XampleJavaMicroservice {
  @RabbitListener(queues = "#{queue1.name}", errorHandler = "xampleErrorHandler")
  public SomeDtoModel receiveMessage(SomeDtoModel model) throws InterruptedException {
    log.info("Received: " + model);
    Thread.sleep(1000);
    return model;
  }
}
