package gov.va.vro;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableJpaRepositories("gov.va.vro.persistence.repository")
@EntityScan("gov.va.vro.persistence.model")
@EnableJpaAuditing
public class VroConfig {
  @Bean
  public RestTemplate getRestTemplate() {
    return new RestTemplate();
  }
}
