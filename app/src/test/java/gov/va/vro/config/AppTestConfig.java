package gov.va.vro.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AppTestConfig {
  @Bean
  public AppTestUtil appTestUtil() {
    return new AppTestUtil();
  }
}
