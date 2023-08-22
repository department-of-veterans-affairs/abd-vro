package gov.va.vro.services.bie.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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

  @Value("${kafka.topic.prefix}")
  String topicPrefix;

  @Getter private Map<String, String> topicToExchangeMap;

  @PostConstruct
  public void addTopicPrefix() {
    topicToExchangeMap =
        kafkaTopicToAmqpExchangeMap.entrySet().stream()
            .collect(Collectors.toMap(e -> topicPrefix + e.getKey(), Map.Entry::getValue));
  }

  public String[] topicNames() {
    return topicToExchangeMap.keySet().stream().toArray(String[]::new);
  }
}
