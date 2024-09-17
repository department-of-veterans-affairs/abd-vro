package gov.va.vro.services.bie.config;

import gov.va.vro.model.biekafka.ContentionEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Getter
@Component
@ConfigurationProperties(prefix = "bie")
@Setter
public class BieKafkaProperties {

  private String kafkaTopicInfix;

  public String[] topicNames() {
    return Arrays.stream(ContentionEvent.values())
        .map(
            contention -> {
              String subString = "CATALOG_" + kafkaTopicInfix + "_CONTENTION";
              return contention.getTopicName().replaceAll("CATALOG_CONTENTION", subString);
            })
        .toArray(String[]::new);
  }
}
