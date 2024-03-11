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
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_ASSOCIATED_TO_CLAIM, CONTENTION_ASSOCIATED_TO_CLAIM",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED, CONTENTION_UPDATED",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_CLASSIFIED, CONTENTION_CLASSIFIED",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_COMPLETED, CONTENTION_COMPLETED",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_DELETED, CONTENTION_DELETED"
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
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_ASSOCIATED_TO_CLAIM, bie-events-contention-associated-to-claim",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED, bie-events-contention-updated",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_CLASSIFIED, bie-events-contention-classified",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_COMPLETED, bie-events-contention-completed",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_DELETED, bie-events-contention-deleted"
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
