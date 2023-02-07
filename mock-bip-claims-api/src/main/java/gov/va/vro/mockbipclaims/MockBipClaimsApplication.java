package gov.va.vro.mockbipclaims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
    exclude = {
      org.springdoc.hateoas.SpringDocHateoasConfiguration.class,
      DataSourceAutoConfiguration.class
    })
public class MockBipClaimsApplication {

  public static void main(String[] args) {
    SpringApplication.run(MockBipClaimsApplication.class, args);
  }
}
