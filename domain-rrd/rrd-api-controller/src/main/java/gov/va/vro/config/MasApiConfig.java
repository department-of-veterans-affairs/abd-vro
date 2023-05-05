package gov.va.vro.config;

import gov.va.vro.service.provider.MasApiProps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configure MAS APIs Related Attributes
@Configuration
public class MasApiConfig {

  @Bean
  MasApiProps masApiConfigLoad(
      @Value("${masAPIProvider.baseURL}") String baseUrl,
      @Value("${masAPIProvider.collectionStatusPath}") String collectionStatusPath,
      @Value("${masAPIProvider.collectionAnnotsPath}") String collectionAnnotsPath,
      @Value("${masAPIProvider.createExamOrderPath}") String createExamOrderPath) {
    return new MasApiProps(
        baseUrl, collectionStatusPath, collectionAnnotsPath, createExamOrderPath);
  }
}
