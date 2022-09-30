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
/*
@OpenAPIDefinition(
    info =
        @Info(
            title = "Virtual Regional Office (VRO) Automated Benefits Delivery (ABD) API",
            description = "APIs to improve benefit delivery services",
            version = "v1",
            license =
                @License(
                    name =
                        "https://github.com/department-of-veterans-affairs/abd-vro/blob/master/LICENSE.md",
                    url = "dung.lam1@va.gov"),
            contact = @Contact(name = "D (Yoom) Lam", email = "dung.lam1@va.gov")),
    servers = {
      @Server(url = "/", description = "Default Server URL"),
      @Server(url = "https://qa.lighthouse.va.gov/abd-vro/", description = "QA Environment"),
      @Server(url = "https://dev.lighthouse.va.gov/abd-vro/", description = "Dev Environment")
    },
    security = {
      @SecurityRequirement(name = "basicAuth"),
      @SecurityRequirement(name = "bearerToken")
    })
 */
public class VroApplication {
  public static void main(String[] args) {
    new SpringApplication(VroApplication.class).run(args);
    log.info("\n-------- VRO App Started ---------");
  }
}
