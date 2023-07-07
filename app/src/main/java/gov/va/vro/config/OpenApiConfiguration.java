package gov.va.vro.config;

import gov.va.vro.config.propmodel.OpenApiProperties;
import gov.va.vro.openapi.spi.CustomSecuritySchemeProvider;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
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

  @Autowired private final OpenApiProperties openApiProperties = new OpenApiProperties();

  /**
   * Configure OpenAPI processor.
   *
   * @return OpenAPI configuration bean
   */
  @Bean
  public OpenAPI customOpenApi() {
    List<Server> servers =
        openApiProperties.getServers().stream()
            .map(server -> new Server().description(server.getDescription()).url(server.getUrl()))
            .collect(Collectors.toList());

    // Adding to this list will have it appear under `tags` at /v3/api-docs
    // Not sure why this list isn't automatically populated when @Tag is used.
    final List<Tag> tags = Arrays.asList(new Tag().name("Xample Domain"));

    OpenAPI config =
        new OpenAPI()
            .info(
                new io.swagger.v3.oas.models.info.Info()
                    .title("VRO App")
                    .description("VRO Java-based application"))
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
