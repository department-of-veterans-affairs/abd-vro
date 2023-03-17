package gov.va.vro.mocklh;

import gov.va.vro.mocklh.config.LhApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({LhApiProperties.class})
public class MockLhApplication {

  public static void main(String[] args) {
    SpringApplication.run(MockLhApplication.class, args);
  }
}
