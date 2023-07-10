package gov.va.vro;

import gov.va.vro.propmodel.Info;
import gov.va.vro.propmodel.OpenApiProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OpenApiConfiguration {
  private final OpenApiProperties openApiProperties;

  @Bean
  public OpenAPI customOpenApi() {
    Info info = openApiProperties.getInfo();
    gov.va.vro.propmodel.Contact contact = info.getContact();
    gov.va.vro.propmodel.License license = info.getLicense();

    List<Server> servers =
        openApiProperties.getServers().stream()
            .map(server -> new Server().description(server.getDescription()).url(server.getUrl()))
            .collect(Collectors.toList());

    OpenAPI config =
        new OpenAPI()
            .info(
                new io.swagger.v3.oas.models.info.Info()
                    .title(info.getTitle())
                    .description(info.getDescription())
                    // .version(info.getVersion())
                    .license(new License().name(license.getName()).url(license.getUrl()))
                    .contact(new Contact().name(contact.getName()).email(contact.getEmail())))
            .servers(servers);
    return config;
  }
}
