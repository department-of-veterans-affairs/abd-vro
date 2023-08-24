package gov.va.vro.model.biekafka;

import java.util.Arrays;

public enum ContentionEvent {
  CONTENTION_ASSOCIATED_TO_CLAIM("CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02"),
  CONTENTION_UPDATED("CONTENTION_BIE_CONTENTION_UPDATED_V02"),
  CONTENTION_CLASSIFIED("CONTENTION_BIE_CONTENTION_CLASSIFIED_V02"),
  CONTENTION_COMPLETED("CONTENTION_BIE_CONTENTION_COMPLETED_V02"),
  CONTENTION_DELETED("CONTENTION_BIE_CONTENTION_DELETED_V02");

  private final String topicName;

  ContentionEvent(String topicName) {
    this.topicName = topicName;
  }

  public String getTopicName() {
    return topicName;
  }

  public static ContentionEvent mapTopicToEvent(String topic) {
    // remove first word prefix from topic seperated by _
    String noPrefixTopic = topic.substring(topic.indexOf("_") + 1);

    return Arrays.stream(ContentionEvent.values())
        .filter(event -> event.getTopicName().equals(noPrefixTopic))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unrecognized topic: " + noPrefixTopic));
  }

  public static String generateRabbitMQChannelName(String topic) {
    return String.format(
        "bie-events-%s", mapTopicToEvent(topic).toString().toLowerCase().replace("_", "-"));
  }
}
