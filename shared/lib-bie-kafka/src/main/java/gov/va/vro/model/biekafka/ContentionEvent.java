package gov.va.vro.model.biekafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ContentionEvent {
  CONTENTION_ASSOCIATED("BIA_SERVICES_BIE_CATALOG_CONTENTION_ASSOCIATED_TO_CLAIM_V02"),
  CONTENTION_UPDATED("BIA_SERVICES_BIE_CATALOG_CONTENTION_UPDATED_V02"),
  CONTENTION_CLASSIFIED("BIA_SERVICES_BIE_CATALOG_CONTENTION_CLASSIFIED_V02"),
  CONTENTION_COMPLETED("BIA_SERVICES_BIE_CATALOG_CONTENTION_COMPLETED_V02"),
  CONTENTION_DELETED("BIA_SERVICES_BIE_CATALOG_CONTENTION_DELETED_V02");

  private final String topicName;

  public static ContentionEvent mapTopicToEvent(String topic) {
    String subString = "CATALOG_.*?_CONTENTION";
    String noSubStringTopic = topic.replaceAll(subString, "CATALOG_CONTENTION");

    return Arrays.stream(ContentionEvent.values())
        .filter(event -> event.getTopicName().equals(noSubStringTopic))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unrecognized topic: " + noSubStringTopic));
  }

  public static String rabbitMqExchangeName(String topic) {
    return String.format(
        "svc_bie_kafka.bie_events_%s", mapTopicToEvent(topic).toString().toLowerCase());
  }
}
