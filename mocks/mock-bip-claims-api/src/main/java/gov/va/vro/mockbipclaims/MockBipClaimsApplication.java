package gov.va.vro.mockbipclaims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"gov.va.vro.mockbipclaims", "gov.va.vro.mockshared"})
public class MockBipClaimsApplication {

  public static void main(String[] args) {
    SpringApplication.run(MockBipClaimsApplication.class, args);
  }
}
