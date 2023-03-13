package gov.va.vro.mockslack.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mockslack.model.SlackMessageStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
