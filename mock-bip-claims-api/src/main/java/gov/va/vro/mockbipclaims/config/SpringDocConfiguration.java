package gov.va.vro.mockbipclaims.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {

  @Bean(name = "org.openapitools.configuration.SpringDocConfiguration.apiInfo")
  OpenAPI apiInfo() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Claims API")
                .description(
                    """
                    <p>This API provides functionality around the C&P Benefit Claim and seeks to
                    provide simple and concise mechanisms to create and view claim information.
                    The functionality falls within two primary areas.<br/> <ul><li>Claim - The
                    ability to create, list, and view summaries for a Veteran's claims.
                    </li><li>Contention -The ability to create, update, delete, and view
                    contention summaries for a given claim.</li></ul> </p> <p>The consumers of
                    this service are expected to participate with other authoritative systems
                    in the VA context. A key design principle with the Claims API is to avoid
                    the one-call-does-everything pattern that has proven to reduce reusability
                    of services and to stovepipe functionality for individual use case. Within
                    the BIP service paradigm, this API would be considered a Process API,
                    designed to encapsulate the specific domain actions around claims.</p>")
                    """)
                .termsOfService("https://developer.va.gov/terms-of-service")
                .license(
                    new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                .version("3.0.0"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "Authorization",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization"))
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
  }
}
