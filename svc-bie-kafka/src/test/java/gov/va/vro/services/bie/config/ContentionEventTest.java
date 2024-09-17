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
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_ASSOCIATED_TO_CLAIM_V02, CONTENTION_ASSOCIATED",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED_V02, CONTENTION_UPDATED",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_CLASSIFIED_V02, CONTENTION_CLASSIFIED",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_COMPLETED_V02, CONTENTION_COMPLETED",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_DELETED_V02, CONTENTION_DELETED"
  })
  public void testMapTopicToEvent_validTopics(String inputTopic, ContentionEvent expectedEvent) {
    assertEquals(expectedEvent, mapTopicToEvent(inputTopic));
  }

  @Test
  public void testMapTopicToEvent_unrecognizedTopic() {
    String topic = "prefix_UNKNOWN_TOPIC";
    assertThrows(IllegalArgumentException.class, () -> mapTopicToEvent(topic));
  }

  @ParameterizedTest
  @CsvSource({
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_ASSOCIATED_TO_CLAIM_V02, svc_bie_kafka.bie_events_contention_associated",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED_V02, svc_bie_kafka.bie_events_contention_updated",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_CLASSIFIED_V02, svc_bie_kafka.bie_events_contention_classified",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_COMPLETED_V02, svc_bie_kafka.bie_events_contention_completed",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_DELETED_V02, svc_bie_kafka.bie_events_contention_deleted"
  })
  public void testGenerateRabbitMQChannelName_channelNames(String inputTopic, String bieChannel) {
    assertEquals(bieChannel, ContentionEvent.rabbitMqExchangeName(inputTopic));
  }

  @Test
  public void testGenerateRabbitMQChannelName_unrecognizedTopic() {
    String topic = "prefix_UNKNOWN_TOPIC";
    assertThrows(IllegalArgumentException.class, () -> ContentionEvent.rabbitMqExchangeName(topic));
  }
}
