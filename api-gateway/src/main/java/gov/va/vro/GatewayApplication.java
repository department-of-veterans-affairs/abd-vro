package gov.va.vro;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "VRO Gateway API", version = "1.0", description = "API for the Gateway"))
public class GatewayApplication {
  public static void main(String[] args) {
    new SpringApplication(GatewayApplication.class).run(args);
    log.info("\n-------- API Gateway Application Started ---------");
  }
}
