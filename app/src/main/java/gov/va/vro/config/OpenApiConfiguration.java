package gov.va.vro.config;

import gov.va.starter.boot.openapi.spi.CustomSecuritySchemeProvider;
import gov.va.vro.config.propmodel.Info;
import gov.va.vro.config.propmodel.OpenApi;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@OpenAPIDefinition
@Configuration
public class OpenApiConfiguration {
  @Autowired(required = false)
  private List<CustomSecuritySchemeProvider> securitySchemeProviders;

  @Autowired private VroProperties props;

  /**
   * Configure OpenAPI processor.
   *
   * @return OpenAPI configuration bean
   */
  @Bean
  public OpenAPI customOpenApi() {
    OpenApi openApi = props.getOpenApi();
    Info info = openApi.getInfo();
    gov.va.vro.config.propmodel.Contact contact = info.getContact();
    gov.va.vro.config.propmodel.License license = info.getLicense();

    List<Server> servers =
        openApi.getServers().stream()
            .map(server -> new Server().description(server.getDescription()).url(server.getUrl()))
            .collect(Collectors.toList());

    // Adding to this list will have it appear under `tags` at http://localhost:8080/v3/api-docs
    // Not sure why this list isn't automatically populated when @Tag is used.
    final List<Tag> tags =
        Arrays.asList(
            new Tag().name("Pdf Generation"),
            new Tag().name("Claim Metrics"),
            new Tag().name("Health Assessment"),
            new Tag().name("MAS Integration"),
            new Tag().name("Xample Domain"));

    OpenAPI config =
        new OpenAPI()
            .info(
                new io.swagger.v3.oas.models.info.Info()
                    .title(info.getTitle())
                    .description(info.getDescription())
                    .version(info.getVersion())
                    .license(new License().name(license.getName()).url(license.getUrl()))
                    .contact(new Contact().name(contact.getName()).email(contact.getEmail())))
            .servers(servers)
            .addSecurityItem(
                new SecurityRequirement()
                    .addList("bearer-jwt", Arrays.asList("read", "write"))
                    .addList("oauth2", Arrays.asList("read", "write")))
            .tags(tags);

    config = configureSecuritySchemes(config);
    return config;
  }

  /**
   * Configure OpenAPI Security schemes.
   *
   * @param config current OpenAPI config object
   * @return OpenAPI config object
   */
  protected OpenAPI configureSecuritySchemes(OpenAPI config) {
    if (null != securitySchemeProviders && securitySchemeProviders.size() > 0) {
      Components securitySchemes = new Components();
      securitySchemeProviders.forEach(
          p -> {
            log.info("Adding SecurityScheme [{}]", p.getName());
            securitySchemes.addSecuritySchemes(p.getName(), p.create());
          });
      config.components(securitySchemes);
    } else {
      log.warn("No SecuritySchemeProviders defined.");
    }
    return config;
  }
}
