package gov.va.vro.services.bie.config;

import static gov.va.vro.model.biekafka.ContentionEvent.mapTopicToEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.vro.model.biekafka.ContentionEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ContentionEventTest {

  @ParameterizedTest
  @CsvSource({
    "TST_CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02, CONTENTION_ASSOCIATED_TO_CLAIM",
    "TST_CONTENTION_BIE_CONTENTION_UPDATED_V02, CONTENTION_UPDATED",
    "TST_CONTENTION_BIE_CONTENTION_CLASSIFIED_V02, CONTENTION_CLASSIFIED",
    "TST_CONTENTION_BIE_CONTENTION_COMPLETED_V02, CONTENTION_COMPLETED",
    "TST_CONTENTION_BIE_CONTENTION_DELETED_V02, CONTENTION_DELETED"
  })
  public void testMapTopicToEvent_validTopics(String inputTopic, ContentionEvent expectedEvent) {
    assertEquals(expectedEvent, mapTopicToEvent(inputTopic));
  }

  @Test
  public void testMapTopicToEvent_unrecognizedTopic() {
    String topic = "prefix_UNKNOWN_TOPIC";
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          mapTopicToEvent(topic);
        });
  }

  @ParameterizedTest
  @CsvSource({
    "TST_CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02, bie-events-contention-associated-to-claim",
    "TST_CONTENTION_BIE_CONTENTION_UPDATED_V02, bie-events-contention-updated",
    "TST_CONTENTION_BIE_CONTENTION_CLASSIFIED_V02, bie-events-contention-classified",
    "TST_CONTENTION_BIE_CONTENTION_COMPLETED_V02, bie-events-contention-completed",
    "TST_CONTENTION_BIE_CONTENTION_DELETED_V02, bie-events-contention-deleted"
  })
  public void testGenerateRabbitMQChannelName_channelNames(String inputTopic, String bieChannel) {
    assertEquals(bieChannel, ContentionEvent.rabbitMqExchangeName(inputTopic));
  }

  @Test
  public void testGenerateRabbitMQChannelName_unrecognizedTopic() {
    String topic = "prefix_UNKNOWN_TOPIC";
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          ContentionEvent.rabbitMqExchangeName(topic);
        });
  }
}
