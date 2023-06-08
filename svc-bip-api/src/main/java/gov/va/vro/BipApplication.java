package gov.va.vro;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
public class BipApplication {
  public static void main(String[] args) {
    new SpringApplication(BipApplication.class).run(args);
    log.info("\n-------- BIP API microservice Started ---------");
  }
}
