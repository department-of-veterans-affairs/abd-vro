package gov.va.vro.mockbipce;

import gov.va.vro.mockbipce.controller.FilesController;
import org.springdoc.hateoas.SpringDocHateoasConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = {SpringDocHateoasConfiguration.class})
@Import(FilesController.class)
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
