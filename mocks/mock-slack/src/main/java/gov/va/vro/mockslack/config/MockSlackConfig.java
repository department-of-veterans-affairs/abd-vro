package gov.va.vro.mockslack.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mockslack.model.SlackMessageStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

@Configuration
public class MockSlackConfig {

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public SlackMessageStore slackMessageStore() {
    return new SlackMessageStore();
  }

  @Bean
  public Pattern collectionIdPattern() {
    return Pattern.compile("^.*collection id\\s*[=:]\\s*(\\d{1,6}).*$", Pattern.CASE_INSENSITIVE);
  }
}
