package gov.va.vro.openapi.provider;

import gov.va.vro.openapi.spi.CustomSecuritySchemeProvider;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    prefix = "starter.openapi.default-jwt-bearer-security-scheme",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class DefaultJwtBearerSecuritySchemeProvider implements CustomSecuritySchemeProvider {

  /**
   * Create the SecurityScheme object for OpenAPI configuration bean.
   *
   * @return created SecurityScheme object
   */
  @Override
  public SecurityScheme create() {

    return new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT");
  }

  /**
   * return the name of the SecurityScheme object.
   *
   * @return name of the SecurityScheme configuration object
   */
  @Override
  public String getName() {
    return "bearer-jwt";
  }
}
