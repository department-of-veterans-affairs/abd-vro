package gov.va.vro.bip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class BipApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(BipApiApplication.class, args);
  }
}
