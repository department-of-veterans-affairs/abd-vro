package gov.va.vro.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configure LH APIs Related Attributes
@Configuration
@Slf4j
public class LhApiConfig {

  @Bean
  LhApiProps lhApiConfigLoad(
      @Value("${lhAPIProvider.tokenValidatorURL}") String tokenValidatorUrl,
      @Value("${lhAPIProvider.vroAudURL}") String vroAudUrl,
      @Value("${lhAPIProvider.apiKey}") String apiKey,
      @Value("${lhAPIProvider.validateToken}") String validateToken) {
    log.info("VRO token/Aud URL " + tokenValidatorUrl + "/" + vroAudUrl);
    return new LhApiProps(tokenValidatorUrl, vroAudUrl, apiKey, validateToken);
  }
}
