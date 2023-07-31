package gov.va.vro.services.bie.service.kafka;

public enum ContentionKafkaEvents {
  CONTENTION_ASSOCIATED_TO_CLAIM("CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02"),
  CONTENTION_UPDATED("CONTENTION_BIE_CONTENTION_UPDATED_V02"),
  CONTENTION_CLASSIFIED("CONTENTION_BIE_CONTENTION_CLASSIFIED_V02"),
  CONTENTION_COMPLETED("CONTENTION_BIE_CONTENTION_COMPLETED_V02"),
  CONTENTION_DELETED("CONTENTION_BIE_CONTENTION_DELETED_V02");

  private final String topicName;

  ContentionKafkaEvents(String topicName) {
    this.topicName = topicName;
  }

  public String getTopicName() {
    return "TST_" + topicName;
  }
}
