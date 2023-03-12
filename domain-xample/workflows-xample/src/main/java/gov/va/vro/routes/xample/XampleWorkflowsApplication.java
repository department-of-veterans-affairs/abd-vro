package gov.va.vro.routes.xample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"gov.va.vro.routes.xample", "gov.va.vro.camel"})
// Needed to interface with the DB
// @EnableJpaRepositories and @EntityScan are needed to interface with the DB
@EnableJpaRepositories("gov.va.vro.persistence.repository")
@EntityScan("gov.va.vro.persistence.model")
// @EnableJpaAuditing is needed to auto-populate created_at and updated_at DB column values --
// https://stackoverflow.com/a/56873616
@EnableJpaAuditing
public class XampleWorkflowsApplication {
  public static void main(String[] args) {
    SpringApplication.run(XampleWorkflowsApplication.class, args);
  }
}
