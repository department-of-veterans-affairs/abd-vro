package gov.va.vro.mockslack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MockSlackApplication {
  public static void main(String[] args) {
    SpringApplication.run(MockSlackApplication.class, args);
  }
}
