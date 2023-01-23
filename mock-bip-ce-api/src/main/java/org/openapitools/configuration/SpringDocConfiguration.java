package org.openapitools.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringDocConfiguration {

    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Claim Evidence API.")
                                .description("The Claim Evidence Application Programming Interface (API) is file service for handling the storage and management of files supporting VA benefit claims. It serves as a modernized point of entry to files previously only accessible through VBMS eFolder. It is designed for easier implementation by consuming systems, but also with the ability to eventually replace the eFolder logic within VBMS. Information on how to generate the JWT token needed for integrating with this service can be found [here](https://github.com/department-of-veterans-affairs/bip-vefs-claimevidence/wiki/JWT-Authorization)")
                                .termsOfService("https://developer.va.gov/terms-of-service")
                                .license(
                                        new License()
                                                .name("Apache 2.0")
                                                .url("https://www.apache.org/licenses/LICENSE-2.0")
                                )
                                .version("0.0.1-SNAPSHOT")
                )
                .components(
                        new Components()
                                .addSecuritySchemes("bearer-key", new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                )
                )
        ;
    }
}