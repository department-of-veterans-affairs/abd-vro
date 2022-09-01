package gov.va.vro.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("gov.va.vro.persistence.repository")
@EntityScan("gov.va.vro.persistence.model")
@EnableJpaAuditing
@PropertySource(value = "classpath:env.properties", ignoreResourceNotFound = true)
public class VroConfig {}
