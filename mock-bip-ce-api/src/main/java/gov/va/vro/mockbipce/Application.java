package gov.va.vro.mockbipce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
    exclude = {
        org.springdoc.hateoas.SpringDocHateoasConfiguration.class,
        DataSourceAutoConfiguration.class
    })
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
