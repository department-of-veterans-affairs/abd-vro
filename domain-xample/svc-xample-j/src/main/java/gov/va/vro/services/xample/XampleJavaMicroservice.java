package gov.va.vro.services.xample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.model.xample.StatusValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class XampleJavaMicroservice {
  @RabbitListener(queues = "#{queue.name}", errorHandler="xampleErrorHandler")
  // @RabbitListener
  public SomeDtoModel receiveMessage(SomeDtoModel model) {
    log.info("Received: " + model);
    try {
      model.setStatus(StatusValue.DONE.name());
    } catch (Throwable t) {
      model.setStatus(StatusValue.ERROR.name());
      model.setReason(t.getMessage());
    }
    return model;
  }

  // @Autowired
  ObjectMapper mapper = new ObjectMapper();

  // @Autowired
  // Jackson2JsonMessageConverter converter;

  public String receiveMessageAAA(byte[] body) {
    log.info("Received: " + body);
    var model = new SomeDtoModel("", "", StatusValue.ERROR.name(), "Huh?");
    try {
      return mapper.writeValueAsString(model);
    } catch (JsonProcessingException e) {
      return e.getMessage();
    }
  }
}
