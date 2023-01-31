package gov.va.vro.mockbipce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
  @Bean
  public JwtProps jwtProps() {
    return new JwtProps();
  }
}
