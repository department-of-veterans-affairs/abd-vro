package gov.va.vro.services.bie.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.va.vro.metricslogging.IMetricLoggerService;
import gov.va.vro.model.biekafka.ContentionEvent;
import gov.va.vro.model.biekafka.ContentionEventPayload;
import gov.va.vro.persistence.model.bieevents.ContentionEventEntity;
import gov.va.vro.services.bie.service.amqp.AmqpMessageSender;
import gov.va.vro.services.bie.service.kafka.ContentionEventsListener;
import gov.va.vro.services.bie.service.repo.ContentionEventsRepo;
import gov.va.vro.services.bie.utils.BieRecordTransformer;
import gov.va.vro.services.bie.utils.ContentionEventPayloadTransformer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ContentionEventsListenerTest {

  @Mock private ContentionEventsRepo contentionEventsRepo;

  @Mock private AmqpMessageSender amqpMessageSender;

  @Mock private IMetricLoggerService metricLogger;
  @Mock private BieRecordTransformer bieRecordTransformer;
  @Mock private ContentionEventPayloadTransformer contentionEventPayloadTransformer;

  private ContentionEventsListener listener;

  @BeforeEach
  public void setup() {
    this.listener =
        new ContentionEventsListener(
            contentionEventsRepo,
            amqpMessageSender,
            metricLogger,
            bieRecordTransformer,
            contentionEventPayloadTransformer);
  }

  @Test
  public void testConsume_WithStringRecord() throws JsonProcessingException {
    final ContentionEventPayload expectedSentMessage =
        ContentionEventPayload.builder()
            .claimId(1L)
            .contentionId(2L)
            .eventType(ContentionEvent.CONTENTION_ASSOCIATED)
            .build();
    Mockito.when(bieRecordTransformer.toContentionEventPayload(Mockito.any()))
        .thenReturn(expectedSentMessage);

    final ContentionEventEntity expectedSavedEntity =
        new ContentionEventPayloadTransformer().toContentionEventEntity(expectedSentMessage);
    Mockito.when(contentionEventPayloadTransformer.toContentionEventEntity(expectedSentMessage))
        .thenReturn(expectedSavedEntity);

    final String topic = ContentionEvent.CONTENTION_ASSOCIATED.getTopicName();
    final String exchange = ContentionEvent.rabbitMqExchangeName(topic);
    final String payload = "{\"claimId\":1, \"contentionId\":2}";
    final ConsumerRecord<String, Object> record = new ConsumerRecord<>(topic, 0, 0, null, payload);
    listener.consume(record);

    Mockito.verify(amqpMessageSender).send(exchange, topic, expectedSentMessage);
    Mockito.verify(contentionEventsRepo).save(expectedSavedEntity);
  }

  @Test
  public void testConsume_WithGenericRecord() throws JsonProcessingException {
    final ContentionEventPayload expectedSentMessage =
        ContentionEventPayload.builder()
            .claimId(1L)
            .contentionId(2L)
            .eventType(ContentionEvent.CONTENTION_ASSOCIATED)
            .build();
    Mockito.when(bieRecordTransformer.toContentionEventPayload(Mockito.any()))
        .thenReturn(expectedSentMessage);

    final ContentionEventEntity expectedSavedEntity =
        new ContentionEventPayloadTransformer().toContentionEventEntity(expectedSentMessage);
    Mockito.when(contentionEventPayloadTransformer.toContentionEventEntity(expectedSentMessage))
        .thenReturn(expectedSavedEntity);

    final String topic = ContentionEvent.CONTENTION_ASSOCIATED.getTopicName();
    final String exchange = ContentionEvent.rabbitMqExchangeName(topic);
    Schema schema =
        new Schema.Parser()
            .parse(
                "{"
                    + "\"type\":\"record\","
                    + "\"name\":\"test\","
                    + "\"fields\":["
                    + "{\"name\":\"ClaimId\",\"type\":\"long\"},{\"name\":\"ContentionId\",\"type\":\"long\"}]}");
    GenericData.Record recordValue = new GenericData.Record(schema);
    recordValue.put("ClaimId", 1L);
    recordValue.put("ContentionId", 2L);
    final ConsumerRecord<String, Object> record =
        new ConsumerRecord<>(topic, 0, 0, null, recordValue);

    listener.consume(record);

    Mockito.verify(amqpMessageSender).send(exchange, topic, expectedSentMessage);
    Mockito.verify(contentionEventsRepo).save(expectedSavedEntity);
  }
}
