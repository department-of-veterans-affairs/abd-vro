package gov.va.vro.mockmas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
    exclude = {
        org.springdoc.hateoas.SpringDocHateoasConfiguration.class,
        DataSourceAutoConfiguration.class
    })
public class MockMasApplication {

  public static void main(String[] args) {
    SpringApplication.run(MockMasApplication.class, args);
  }
}
