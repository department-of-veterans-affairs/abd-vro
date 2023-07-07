package gov.va.vro;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class BipApplication {
  public static void main(String[] args) {
    new SpringApplication(BipApplication.class).run(args);
    log.info("\n-------- BIP API microservice Started ---------");
  }
}
