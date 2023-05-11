package gov.va.vro.openapi.spi;

import io.swagger.v3.oas.models.security.Scopes;

public interface CustomOauthScopeConfigurer {

  /** Configure the Scope object for OpenAPI configuration bean. */
  void configure(Scopes scope);
}
