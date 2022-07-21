package gov.va.vro.service.db;

import org.springdoc.hateoas.SpringDocHateoasConfiguration;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = {"gov.va.vro.service.db"},
    exclude = {SpringDocHateoasConfiguration.class, SwaggerConfig.class})
@EnableJpaRepositories("gov.va.vro.persistence.repository")
@EntityScan("gov.va.vro.persistence.model")
public class TestConfig {}
