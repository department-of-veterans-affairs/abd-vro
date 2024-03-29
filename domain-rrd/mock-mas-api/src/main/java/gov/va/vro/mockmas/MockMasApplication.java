package gov.va.vro.mockmas;

import gov.va.vro.mockmas.config.MasApiProperties;
import gov.va.vro.mockmas.config.MasOauth2Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({MasApiProperties.class, MasOauth2Properties.class})
public class MockMasApplication {

  public static void main(String[] args) {
    SpringApplication.run(MockMasApplication.class, args);
  }
}
