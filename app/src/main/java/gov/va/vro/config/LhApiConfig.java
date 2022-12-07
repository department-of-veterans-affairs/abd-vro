package gov.va.vro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configure LH APIs Related Attributes
@Configuration
public class LhApiConfig {

  @Bean
  LhApiProps lhApiConfigLoad(
      @Value("${lhAPIProvider.tokenValidatorURL}") String tokenValidatorURL,
      @Value("${lhAPIProvider.vroAudURL}") String vroAudURL,
      @Value("${lhAPIProvider.apiKey}") String apiKey,
      @Value("${lhAPIProvider.validateToken}") String validateToken) {
    return new LhApiProps(tokenValidatorURL, vroAudURL, validateToken, apiKey);
  }
}
