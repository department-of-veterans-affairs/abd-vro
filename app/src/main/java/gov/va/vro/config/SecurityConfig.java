package gov.va.vro.config;

import gov.va.vro.security.ApiAuthKeyFilter;
import gov.va.vro.security.ApiAuthKeyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${apiauth.hdr-key-name}")
  private String apiKeyAuthHeaderName;

  @Value("${apiauth.url-context}")
  private String urlContext;

  private final ApiAuthKeyManager apiAuthKeyManager;

  protected void configure(HttpSecurity httpSecurity) throws Exception {

    ApiAuthKeyFilter apiAuthKeyFilter = new ApiAuthKeyFilter(apiKeyAuthHeaderName);
    apiAuthKeyFilter.setAuthenticationManager(apiAuthKeyManager);

    // Secure end point
    httpSecurity
        .antMatcher(urlContext)
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
