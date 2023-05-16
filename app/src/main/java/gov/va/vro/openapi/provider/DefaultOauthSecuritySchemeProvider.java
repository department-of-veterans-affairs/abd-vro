package gov.va.vro.openapi.provider;

import gov.va.vro.openapi.spi.CustomOauthScopeConfigurer;
import gov.va.vro.openapi.spi.CustomSecuritySchemeProvider;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(
    prefix = "starter.openapi.default-oauth-security-scheme",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class DefaultOauthSecuritySchemeProvider implements CustomSecuritySchemeProvider {

  private String oauthUrl;
  private List<CustomOauthScopeConfigurer> scopeConfigurers;

  @Autowired
  public DefaultOauthSecuritySchemeProvider(
      @Value("${oauthUrl:http://idp.va.gov/}") String oauthUrl,
      List<CustomOauthScopeConfigurer> scopeConfigurers) {
    this.oauthUrl = oauthUrl;
    this.scopeConfigurers = scopeConfigurers;
  }

  /**
   * Create the SecurityScheme object for OpenAPI configuration bean.
   *
   * @return created SecurityScheme object
   */
  public SecurityScheme create() {

    Scopes scopes = new Scopes();
    if (null != scopeConfigurers) {
      scopeConfigurers.forEach(s -> s.configure(scopes));
    }

    return new SecurityScheme()
        .type(SecurityScheme.Type.OAUTH2)
        .flows(
            new OAuthFlows().implicit(new OAuthFlow().authorizationUrl(oauthUrl).scopes(scopes)));
  }

  /**
   * return the name of the SecurityScheme object.
   *
   * @return name of the SecurityScheme configuration object
   */
  public String getName() {
    return "oauth2";
  }
}
