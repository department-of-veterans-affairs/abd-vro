package gov.va.vro.services.bie.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// @EnableJpaRepositories and @EntityScan are needed to interface with the DB
@EnableJpaRepositories("gov.va.vro.persistence.repository.bieevent")
@EntityScan("gov.va.vro.persistence.model.bieevents")
// @EnableJpaAuditing is needed to auto-populate created_at and updated_at DB column values --
// https://stackoverflow.com/a/56873616
@EnableJpaAuditing
@Configuration
public class RepoConfig {}
