package gov.va;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"gov.va"})
@EnableJpaAuditing
public class PersistenceTestConfig {}
