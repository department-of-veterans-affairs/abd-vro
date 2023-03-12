package gov.va.vro.services.xample;

import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.model.xample.StatusValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class XampleJavaMicroservice {
  @RabbitListener(queues = "#{queue.name}", errorHandler="xampleErrorHandler")
  public SomeDtoModel receiveMessage(SomeDtoModel model) {
    log.info("Received: " + model);
    try {
      Thread.sleep(1000);
      model.setStatus(StatusValue.DONE.name());
    } catch (Throwable t) {
      model.setStatus(StatusValue.ERROR.name());
      model.setReason(t.getMessage());
    }
    return model;
  }
}
