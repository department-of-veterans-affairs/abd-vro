package gov.va.vro.model.biekafka;

import java.util.Arrays;

public enum ContentionEvent {
  CONTENTION_ASSOCIATED_TO_CLAIM("BIA_SERVICES_BIE_CATALOG_CONTENTION_ASSOCIATED_TO_CLAIM"),
  CONTENTION_UPDATED("BIA_SERVICES_BIE_CATALOG_CONTENTION_UPDATED"),
  CONTENTION_CLASSIFIED("BIA_SERVICES_BIE_CATALOG_CONTENTION_CLASSIFIED"),
  CONTENTION_COMPLETED("BIA_SERVICES_BIE_CATALOG_CONTENTION_COMPLETED"),
  CONTENTION_DELETED("BIA_SERVICES_BIE_CATALOG_CONTENTION_DELETED");

  private final String topicName;

  ContentionEvent(String topicName) {
    this.topicName = topicName;
  }

  public String getTopicName() {
    return topicName;
  }

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
        "bie-events-%s", mapTopicToEvent(topic).toString().toLowerCase().replace("_", "-"));
  }
}
