package gov.va.vro;

import gov.va.starter.boot.openapi.config.OpenApiConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@Slf4j
@SpringBootConfiguration
@ConfigurationPropertiesScan(basePackages = {"gov.va.vro.config"})
@EnableAutoConfiguration(exclude = {OpenApiConfiguration.class})
@ComponentScan(
    basePackages = {"gov.va.vro", "gov.va.starter.boot"},
    excludeFilters = {
      @ComponentScan.Filter(
          type = FilterType.ASSIGNABLE_TYPE,
          value = {OpenApiConfiguration.class})
    })
public class VroApplication {
  public static void main(String[] args) {
    new SpringApplication(VroApplication.class).run(args);
    log.info("\n-------- VRO App Started ---------");
  }
}
