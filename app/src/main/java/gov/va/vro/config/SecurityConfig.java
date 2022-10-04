package gov.va.vro.config;

import gov.va.vro.security.ApiAuthKeyFilter;
import gov.va.vro.security.ApiAuthKeyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Slf4j
@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

  @Value("${apiauth.hdr-key-name}")
  private String apiKeyAuthHeaderName;

  @Value("${apiauth.url-context}")
  private String urlContext;

  private final ApiAuthKeyManager apiAuthKeyManager;

  /**
   * Sets the security filter chain.
   *
   * @param httpSecurity http security.
   * @return a filter chain.
   * @throws Exception when error occurs.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    ApiAuthKeyFilter apiAuthKeyFilter = new ApiAuthKeyFilter(apiKeyAuthHeaderName);
    apiAuthKeyFilter.setAuthenticationManager(apiAuthKeyManager);

    httpSecurity
        .exceptionHandling()
        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
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
    return httpSecurity.build();
  }
}
