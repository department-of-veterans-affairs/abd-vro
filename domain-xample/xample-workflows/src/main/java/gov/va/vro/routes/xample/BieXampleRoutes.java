package gov.va.vro.routes.xample;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.biekafka.BieMessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BieXampleRoutes {
  private final DbHelper dbHelper;
  private final ObjectMapper objectMapper;

  @RabbitListener(
      queues = {
        "saveToDB-bie-events-contention-associated",
        "saveToDB-bie-events-contention-updated",
        "saveToDB-bie-events-contention-classified",
        "saveToDB-bie-events-contention-completed",
        "saveToDB-bie-events-contention-deleted"
      })
  public void handleMessage(BieMessagePayload payload) {
    try {
      dbHelper.saveContentionEvent(payload);
      payload.setStatus(200);
      log.info("Saved Contention Event to DB");

      String jsonBody = objectMapper.writeValueAsString(payload);
      log.info("ReceivedMessageEventBody: " + jsonBody);
    } catch (Exception e) {
      log.error("Error processing message", e);
      handleException(payload, e);
    }
  }

  private void handleException(BieMessagePayload payload, Exception e) {
    payload.setStatus(500);
    payload.setStatusMessage(e.toString());
    try {
      String jsonBody = objectMapper.writeValueAsString(payload);
      log.error("FailedMessageEventBody: " + jsonBody);
    } catch (Exception jsonException) {
      log.error("Error converting failed message to JSON", jsonException);
    }
  }
}
