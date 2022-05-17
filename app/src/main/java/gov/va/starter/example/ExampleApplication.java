package gov.va.starter.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j // lombok creates our logger as 'log' for us
@SpringBootApplication(scanBasePackages = {"gov.va.starter.example", "gov.va.starter.boot"})
public class ExampleApplication {

  /**
   * main function.
   *
   * @param args command line args
   */
  public static void main(String[] args) {
    new SpringApplication(ExampleApplication.class).run(args);
    log.info("\n\n\n\n\n---------------Example API Started.----------------\n\n\n\n\n");
  }
}
