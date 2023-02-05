package org.openapitools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
    exclude = {
    org.springdoc.hateoas.SpringDocHateoasConfiguration.class,
    DataSourceAutoConfiguration.class
})

@ComponentScan(
    basePackages = {"org.openapitools", "org.openapitools.api", "org.openapitools.configuration"},
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class OpenApiGeneratorApplication {

  public static void main(String[] args) {
    SpringApplication.run(OpenApiGeneratorApplication.class, args);
  }
}
