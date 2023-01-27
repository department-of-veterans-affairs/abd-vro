package gov.va.vro.mockbipce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(
    exclude = {
        org.springdoc.hateoas.SpringDocHateoasConfiguration.class
    },
    excludeName = {
        "org.apache.camel.spring.boot.CamelAutoConfiguration"
    })
@ComponentScan(
    basePackages = {
      "org.openapitools",
      "gov.va.vro.mockbipce.api",
      "org.openapitools.configuration"
    })
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
