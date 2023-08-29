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

  @Getter String kakfaTopicPrefix;

  public String[] topicNames() {
    return Arrays.stream(ContentionEvent.values())
        .map(contention -> kakfaTopicPrefix + contention.getTopicName())
        .toArray(String[]::new);
  }
}
