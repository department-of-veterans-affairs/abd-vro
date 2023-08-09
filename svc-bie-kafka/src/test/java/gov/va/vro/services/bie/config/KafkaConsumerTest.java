package gov.va.vro.services.bie.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.vro.model.biekafka.ContentionEvent;
import gov.va.vro.services.bie.service.AmqpMessageSender;
import gov.va.vro.services.bie.service.kafka.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class KafkaConsumerTest {

  @Mock private ConsumerFactory<?, ?> consumerFactory;
  @Mock private AmqpMessageSender amqpMessageSender;

  @Autowired private BieProperties bieProperties;

  @Autowired private KafkaConsumer kafkaConsumer;

  @BeforeEach
  void setUp() {
    bieProperties = new BieProperties();
  }

  @ParameterizedTest
  @CsvSource({
    "TST_CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02, CONTENTION_ASSOCIATED_TO_CLAIM",
    "TST_CONTENTION_BIE_CONTENTION_UPDATED_V02, CONTENTION_UPDATED",
    "TST_CONTENTION_BIE_CONTENTION_CLASSIFIED_V02, CONTENTION_CLASSIFIED",
    "TST_CONTENTION_BIE_CONTENTION_COMPLETED_V02, CONTENTION_COMPLETED",
    "TST_CONTENTION_BIE_CONTENTION_DELETED_V02, CONTENTION_DELETED"
  })
  public void testMapTopicToEvent_validTopics(String inputTopic, ContentionEvent expectedEvent) {
    assertEquals(expectedEvent, kafkaConsumer.mapTopicToEvent(inputTopic));
  }

  @Test
  public void testMapTopicToEvent_unrecognizedTopic() {
    String topic = "prefix_UNKNOWN_TOPIC";
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          kafkaConsumer.mapTopicToEvent(topic);
        });
  }
}
