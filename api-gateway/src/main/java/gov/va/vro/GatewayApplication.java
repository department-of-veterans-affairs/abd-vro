package gov.va.vro;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = {"gov.va.vro"})
public class GatewayApplication {
  public static void main(String[] args) {
    new SpringApplication(GatewayApplication.class).run(args);
    log.info("\n-------- API Gateway Application Started ---------");
  }
}
