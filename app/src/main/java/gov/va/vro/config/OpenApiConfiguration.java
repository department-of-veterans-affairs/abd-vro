package gov.va.vro.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.starter.boot.openapi.spi.CustomSchemaProvider;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;

@Slf4j
@OpenAPIDefinition
@Configuration
public class OpenApiConfiguration {
  @Value("${serverUrl:http://localhost:8080}")
  private String serverUrl;

  @Value("${oauthUrl:http://idp.va.gov/}")
  private String oauthUrl;

  @Autowired(required = false)
  private List<CustomSchemaProvider> schemaProviders;

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
    ObjectMapper mapper = new ObjectMapper();
    try {
      log.info(mapper.writeValueAsString(props));
    } catch (Exception e) {
    }

    OpenApi openApi = props.getOpenApi();
    Info info = openApi.getInfo();
    gov.va.vro.config.propmodel.Contact contact = info.getContact();
    gov.va.vro.config.propmodel.License license = info.getLicense();

    OpenAPI config =
        new OpenAPI()
            .info(
                new io.swagger.v3.oas.models.info.Info()
                    .title(info.getTitle())
                    .description(info.getDescription())
                    .version(info.getVersion())
                    .license(new License().name(license.getName()).url(license.getUrl()))
                    .contact(new Contact().name(contact.getName()).email(contact.getEmail())))
            .servers(Arrays.asList(new Server().url(serverUrl)))
            .addSecurityItem(
                new SecurityRequirement()
                    .addList("bearer-jwt", Arrays.asList("read", "write"))
                    .addList("oauth2", Arrays.asList("read", "write")));

    config = configureSecuritySchemes(config);
    config = configureSchemas(config);
    return config;
  }

  @PostConstruct
  public void xxxx() {
    ObjectMapper mapper = new ObjectMapper();
    log.info("****************************");
    try {
      log.info(mapper.writeValueAsString(props));
    } catch (Exception e) {
      log.info(e.getMessage());
    }
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

  /**
   * Configure OpenAPI Schemas.
   *
   * @param config current OpenAPI config object
   * @return OpenAPI config object
   */
  protected OpenAPI configureSchemas(OpenAPI config) {
    if (null != schemaProviders && schemaProviders.size() > 0) {
      Components schemas = new Components();
      schemaProviders.stream()
          .forEach(
              p -> {
                log.info("Adding Schema [{}]", p.getName());
                schemas.addSchemas(p.getName(), p.create());
              });
      config.components(schemas);
    } else {
      log.info("No SchemaProviders defined.");
    }

    return config;
  }
}
