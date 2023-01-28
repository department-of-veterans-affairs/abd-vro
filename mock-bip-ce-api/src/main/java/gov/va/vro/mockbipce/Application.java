package gov.va.vro.mockbipce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(
    exclude = {
        org.springdoc.hateoas.SpringDocHateoasConfiguration.class
    })
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
