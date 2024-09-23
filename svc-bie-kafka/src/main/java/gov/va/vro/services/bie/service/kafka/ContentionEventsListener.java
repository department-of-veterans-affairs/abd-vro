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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.ComponentScan;
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
  private static final String METRICS_PREFIX = "vro_bie_kafka";

  final String[] metricTagsSendToQueue = new String[] {"type:sendRecordToMq", "source:svcBieKafka"};

  @KafkaListener(topics = "#{bieKafkaProperties.topicNames()}")
  public void consume(ConsumerRecord<String, Object> record) {

    // TODO: Object needs to be converted to GenericRecord once local Kafka schema registry
    //  mocks are implemented for testing purposes.
    long transactionStartTime = System.nanoTime();

    try {
      String topicName = record.topic();
      String[] metricTagsWithTopicName =
          ArrayUtils.addAll(metricTagsSendToQueue, new String[] {"topic:" + topicName});

      ContentionEventPayload payload = bieRecordTransformer.toContentionEventPayload(record);
      ContentionEventEntity toSave =
          contentionEventPayloadTransformer.toContentionEventEntity(payload);

      contentionEventsRepo.save(toSave);

      amqpMessageSender.send(ContentionEvent.rabbitMqExchangeName(topicName), topicName, payload);

      submitMetrics(metricTagsWithTopicName, transactionStartTime);
    } catch (Exception e) {
      log.error("Exception occurred while processing message: " + e.getMessage());
      metricLogger.submitCount(
          METRICS_PREFIX, MetricLoggerService.METRIC.RESPONSE_ERROR, metricTagsSendToQueue);
    }
  }

  private void submitMetrics(String[] metricTagsWithTopicName, long transactionStartTime) {
    metricLogger.submitCount(
        METRICS_PREFIX, MetricLoggerService.METRIC.REQUEST_START, metricTagsWithTopicName);
    metricLogger.submitRequestDuration(
        METRICS_PREFIX, transactionStartTime, System.nanoTime(), metricTagsWithTopicName);
    metricLogger.submitCount(
        METRICS_PREFIX, MetricLoggerService.METRIC.RESPONSE_COMPLETE, metricTagsWithTopicName);
  }
}
