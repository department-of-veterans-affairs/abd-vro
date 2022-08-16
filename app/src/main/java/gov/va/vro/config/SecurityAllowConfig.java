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

@Slf4j
@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class SecurityAllowConfig extends WebSecurityConfigurerAdapter {

  @Value("${apiauth.hdr-key-name}")
  private String API_KEY_AUTH_HEADER_NAME;

  @Value("${apiauth.url-context}")
  private String URL_CONTEXT;

  private final ApiAuthKeyManager apiAuthKeyManager;

  protected void configure(HttpSecurity httpSecurity) throws Exception {

    ApiAuthKeyFilter apiAuthKeyFilter = new ApiAuthKeyFilter(API_KEY_AUTH_HEADER_NAME);
    apiAuthKeyFilter.setAuthenticationManager(apiAuthKeyManager);

    // Secure end point
    httpSecurity.antMatcher(URL_CONTEXT).csrf().disable();
    // TODO: Will re-enable once we update swagger to handle this.
    // In the meantime, we need to disable security to continue testing
    //        .sessionManagement()
    //        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    //        .and()
    //        .addFilter(apiAuthKeyFilter)
    //        .authorizeRequests()
    //        .anyRequest()
    //        .authenticated();
  }
}
