package gov.va.vro;

import gov.va.vro.security.ApiAuthKeyFilter;
import gov.va.vro.security.ApiAuthKeyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Slf4j
@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityAllowConfig extends WebSecurityConfigurerAdapter {

  @Value("${apiauth.keyhdrname}")
  private String API_KEY_AUTH_HEADER_NAME;

  @Value("${apiauth.urlcontext}")
  private String URL_CONTEXT;
  private ApiAuthKeyManager apiAuthKeyManager;

  @Autowired
  public void setApiAuthKeyManager(ApiAuthKeyManager apiAuthKeyManager) {
    this.apiAuthKeyManager = apiAuthKeyManager;
  }

  protected void configure(HttpSecurity httpSecurity) throws Exception {

    ApiAuthKeyFilter apiAuthKeyFilter = new ApiAuthKeyFilter(API_KEY_AUTH_HEADER_NAME);
    apiAuthKeyFilter.setAuthenticationManager(apiAuthKeyManager);

    // Secure end point
    httpSecurity
            .antMatcher(URL_CONTEXT)
            .csrf()
            .disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(apiAuthKeyFilter)
            .authorizeRequests()
            .anyRequest()
            .authenticated();
  }
}