package gov.va.vro.mocklh;

import gov.va.vro.mocklh.config.LhApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({LhApiProperties.class})
public class MockLhApplication {

  public static void main(String[] args) {
    SpringApplication.run(MockLhApplication.class, args);
  }
}
