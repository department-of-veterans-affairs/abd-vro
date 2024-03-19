package gov.va.vro.services.bie.config;

import gov.va.vro.model.biekafka.ContentionEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@ConfigurationProperties(prefix = "bie")
@Setter
public class BieProperties {

  @Getter String kafkaTopicEnv;

  public String[] topicNames() {
    return Arrays.stream(ContentionEvent.values())
        .map(
            contention -> {
              String subString = "CATALOG_" + kafkaTopicEnv + "_CONTENTION";
              return contention.getTopicName().replaceAll("CATALOG_CONTENTION", subString);
            })
        .toArray(String[]::new);
  }
}
