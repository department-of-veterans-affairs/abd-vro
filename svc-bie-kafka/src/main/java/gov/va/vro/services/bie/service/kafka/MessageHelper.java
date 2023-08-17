package gov.va.vro.services.bie.service.kafka;

import gov.va.vro.model.biekafka.ContentionEvent;

import java.util.Arrays;

public class MessageHelper {
  public static ContentionEvent mapTopicToEvent(String topic) {
    // remove first word prefix from topic seperated by _
    String noPrefixTopic = topic.substring(topic.indexOf("_") + 1);

    return Arrays.stream(ContentionEvent.values())
        .filter(event -> event.getTopicName().equals(noPrefixTopic))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unrecognized topic: " + noPrefixTopic));
  }
}
