package gov.va.vro.services.bie.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "bie")
@Setter
@Getter
public class BieProperties {

  /**
   * Map of entries where the key is a kafka topic pattern (regex) to which this app will subscribe.
   * This pattern will be prepended and appended with wildcards to allow for any prefix or suffix in
   * the kafka topic name. The value is the corresponding RabbitMQ exchange/queue upon which the
   * payload will be put. These values are separated by a colon ":" character.
   */
  private Map<String, String> topicMap;
}
