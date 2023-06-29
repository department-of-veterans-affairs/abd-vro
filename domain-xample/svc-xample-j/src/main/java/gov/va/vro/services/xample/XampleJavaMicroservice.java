package gov.va.vro.services.xample;

import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.model.xample.StatusValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class XampleJavaMicroservice {
  @RabbitListener(queues = "#{queue1.name}", errorHandler="xampleErrorHandler")
  public SomeDtoModel receiveMessage(SomeDtoModel model) {
    log.info("Received: " + model);
    try {
      Thread.sleep(1000);
      // To test an error response, throw exception if resourceId is not an integer
      Integer.parseInt(model.getResourceId());
      model.status(StatusValue.DONE);
      model.getHeader().setStatusCode(200);
    } catch (Throwable t) {
      log.error("Simulated error: "+t);
      model.header(417, t.toString());
    }
    return model;
  }
}
