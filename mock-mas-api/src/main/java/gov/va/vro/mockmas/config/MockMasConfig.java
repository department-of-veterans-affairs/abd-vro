package gov.va.vro.mockmas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MockMasConfig {

  /**
   * Creates and provides the common instance of RestTemplate as a bean for the application.
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
