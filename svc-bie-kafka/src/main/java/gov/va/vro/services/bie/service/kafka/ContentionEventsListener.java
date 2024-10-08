package gov.va.vro.services.bie.service.kafka;

import gov.va.vro.metricslogging.IMetricLoggerService;
import gov.va.vro.metricslogging.MetricLoggerService;
import gov.va.vro.model.biekafka.ContentionEvent;
import gov.va.vro.model.biekafka.ContentionEventPayload;
import gov.va.vro.persistence.model.bieevents.ContentionEventEntity;
import gov.va.vro.services.bie.service.amqp.AmqpMessageSender;
import gov.va.vro.services.bie.service.repo.ContentionEventsRepo;
import gov.va.vro.services.bie.utils.BieRecordTransformer;
import gov.va.vro.services.bie.utils.ContentionEventPayloadTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.amqp.AmqpException;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@ComponentScan("gov.va.vro.metricslogging")
public class ContentionEventsListener {

  private final ContentionEventsRepo contentionEventsRepo;
  private final AmqpMessageSender amqpMessageSender;
  private final IMetricLoggerService metricLogger;
  private final BieRecordTransformer bieRecordTransformer;
  private final ContentionEventPayloadTransformer contentionEventPayloadTransformer;

  @KafkaListener(topics = "#{bieKafkaProperties.topicNames()}")
  public void consume(ConsumerRecord<String, Object> record) {

    // TODO: Object needs to be converted to GenericRecord once local Kafka schema registry
    //  mocks are implemented for testing purposes.
    long transactionStartTime = System.nanoTime();

    final String topicName = record.topic();
    final String[] metricTags = {"topic:" + topicName};

    boolean saved = false;
    boolean sent = false;
    ContentionEventPayload payload = null;
    try {
      String exchange = ContentionEvent.rabbitMqExchangeName(topicName);
      payload = bieRecordTransformer.toContentionEventPayload(record);
      ContentionEventEntity toSave =
          contentionEventPayloadTransformer.toContentionEventEntity(payload);

      try {
        contentionEventsRepo.save(toSave);
        saved = true;
      } catch (DataAccessException e) {
        log.error("Database error while saving message: {}", e.getMessage());
        metricLogger.submitCount(MetricLoggerService.METRIC.RESPONSE_ERROR, metricTags);
        return;
      }

      try {
        amqpMessageSender.send(exchange, topicName, payload);
        sent = true;
      } catch (AmqpException e) {
        log.error("AMQP error while sending message: {}", e.getMessage());
        metricLogger.submitCount(MetricLoggerService.METRIC.RESPONSE_ERROR, metricTags);
      }

      submitMetrics(metricTags, transactionStartTime);
    } catch (Exception e) {
      log.error("General error while processing message: {}", e.getMessage());
      metricLogger.submitCount(MetricLoggerService.METRIC.RESPONSE_ERROR, metricTags);
    } finally {
      log.info(
          "event=receivedMessage topic={} saved={} sent={} payload={}",
          topicName,
          saved,
          sent,
          payload);
    }
  }

  private void submitMetrics(String[] metricTagsWithTopicName, long transactionStartTime) {
    metricLogger.submitCount(MetricLoggerService.METRIC.REQUEST_START, metricTagsWithTopicName);
    metricLogger.submitRequestDuration(
        transactionStartTime, System.nanoTime(), metricTagsWithTopicName);
    metricLogger.submitCount(MetricLoggerService.METRIC.RESPONSE_COMPLETE, metricTagsWithTopicName);
  }
}
