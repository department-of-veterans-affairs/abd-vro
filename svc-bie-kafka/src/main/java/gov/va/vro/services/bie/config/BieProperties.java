package gov.va.vro.services.bie.config;

import gov.va.vro.model.biekafka.ContentionEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "bie")
@Setter
public class BieProperties {

  /**
   * Map of entries where the keys are the kafka topics to which this app will subscribe. The value
   * is the corresponding RabbitMQ exchange upon which the payload will be put. These values are
   * separated by a colon ":" character.
   */
  private Map<String, String> kafkaTopicToAmqpExchangeMap;

  @Getter String kakfaTopicPrefix;

  @Getter private Map<String, String> topicToExchangeMap;

  @PostConstruct
  public void addPrefixToTopicNames() {
    topicToExchangeMap =
        kafkaTopicToAmqpExchangeMap.entrySet().stream()
            .collect(Collectors.toMap(e -> kakfaTopicPrefix + e.getKey(), Map.Entry::getValue));
  }

  public String[] topicNames() {
    return Arrays.stream(ContentionEvent.values())
        .map(contention -> kakfaTopicPrefix + contention.getTopicName())
        .toArray(String[]::new);
  }
}
