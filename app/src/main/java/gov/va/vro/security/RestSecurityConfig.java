package gov.va.vro.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String API_KEY_AUTH_HEADER_NAME = "X-API-Key";

  @Autowired
  private ApiAuthKeyManager apiAuthKeyManager;

  protected void configure(HttpSecurity httpSecurity) throws Exception {

    ApiAuthKeyFilter apiAuthKeyFilter = new ApiAuthKeyFilter(API_KEY_AUTH_HEADER_NAME);
    apiAuthKeyFilter.setAuthenticationManager(apiAuthKeyManager);

    // disable CSRF
    httpSecurity
        .antMatcher("/v1/demo/**")
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
