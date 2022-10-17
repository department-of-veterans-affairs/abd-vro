package gov.va.vro.abddataaccess;

import org.springdoc.hateoas.SpringDocHateoasConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {SpringDocHateoasConfiguration.class})
public class AbdApplication {
  public static void main(String[] args) {
    SpringApplication.run(AbdApplication.class, args);
  }
}
