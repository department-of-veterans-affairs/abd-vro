package gov.va.vro.routes.xample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.metricslogging.IMetricLoggerService;
import gov.va.vro.model.biekafka.ContentionEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ComponentScan("gov.va.vro.metricslogging")
public class BieXampleRoutes {

  private static final String METRICS_PREFIX = "vro_xample_workflows";
  private final ObjectMapper objectMapper;
  private final IMetricLoggerService metricLogger;

  private final String[] metricTagsSaveContentionEvent =
      new String[] {"type:saveContentionEvent", "source:xampleWorkflows"};

  @RabbitListener(
      queues = {
        "saveToDB-bie-events-contention-associated",
        "saveToDB-bie-events-contention-updated",
        "saveToDB-bie-events-contention-classified",
        "saveToDB-bie-events-contention-completed",
        "saveToDB-bie-events-contention-deleted"
      })
  public void handleMessage(ContentionEventPayload payload) {
    try {
      metricLogger.submitCount(
          METRICS_PREFIX, IMetricLoggerService.METRIC.REQUEST_START, metricTagsSaveContentionEvent);
      long transactionStartTime = System.nanoTime();

      metricLogger.submitRequestDuration(
          METRICS_PREFIX, transactionStartTime, System.nanoTime(), metricTagsSaveContentionEvent);
      metricLogger.submitCount(
          METRICS_PREFIX,
          IMetricLoggerService.METRIC.RESPONSE_COMPLETE,
          metricTagsSaveContentionEvent);

      log.info("Saved Contention Event to DB");
      payload.setStatus(200);
      String jsonBody = objectMapper.writeValueAsString(payload);
      log.info("ReceivedMessageEventBody: " + jsonBody);
    } catch (Exception e) {
      log.error("Error processing message", e);
      handleException(payload, e);
    }
  }

  private void handleException(ContentionEventPayload payload, Exception e) {
    payload.setStatus(500);
    payload.setStatusMessage(e.toString());
    try {
      String jsonBody = objectMapper.writeValueAsString(payload);
      log.error("FailedMessageEventBody: " + jsonBody);

      metricLogger.submitCount(
          METRICS_PREFIX,
          IMetricLoggerService.METRIC.RESPONSE_ERROR,
          metricTagsSaveContentionEvent);
    } catch (JsonProcessingException jsonException) {
      log.error("Error converting failed message to JSON", jsonException);
    } catch (Exception exception) {
      log.error("Error processing exception", exception);
    }
  }
}
