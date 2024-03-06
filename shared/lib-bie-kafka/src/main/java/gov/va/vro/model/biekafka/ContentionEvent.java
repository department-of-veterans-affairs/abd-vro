package gov.va.vro.model.biekafka;

import java.util.Arrays;

public enum ContentionEvent {
  CONTENTION_ASSOCIATED_TO_CLAIM("BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_ASSOCIATED_TO_CLAIM"),
  CONTENTION_UPDATED("BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED"),
  CONTENTION_CLASSIFIED("BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_CLASSIFIED"),
  CONTENTION_COMPLETED("BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_COMPLETED"),
  CONTENTION_DELETED("BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_DELETED");

  private final String topicName;

  ContentionEvent(String topicName) {
    this.topicName = topicName;
  }

  public String getTopicName() {
    return topicName;
  }

  public static ContentionEvent mapTopicToEvent(String topic) {
    // TODO(3/5/24): Add a better
    String prefixPattern = "^EXT_VRO_[A-Z]+_";
    String noPrefixTopic = topic.replaceFirst(prefixPattern, "");
    return Arrays.stream(ContentionEvent.values())
        .filter(event -> event.getTopicName().equals(noPrefixTopic))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unrecognized topic: " + noPrefixTopic));
  }

  public static String rabbitMqExchangeName(String topic) {
    return String.format(
        "bie-events-%s", mapTopicToEvent(topic).toString().toLowerCase().replace("_", "-"));
  }
}
