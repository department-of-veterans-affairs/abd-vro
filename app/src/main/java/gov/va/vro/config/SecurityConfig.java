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
public class SecurityConfig {

  @Value("${apiauth.hdr-key-name-v1}")
  private String apiKeyAuthHeaderName;

  @Value("${apiauth.url-context-v1}")
  private String urlContextV1;

  @Value("${apiauth.hdr-key-name-v2}")
  private String jwtAuthHeaderName;

  @Value("${apiauth.url-context-v2}")
  private String urlContextV2;

  private final ApiAuthKeyManager apiAuthKeyManager;

  /**
   * Sets the security filter chain.
   *
   * @param httpSecurity http security.
   * @return a filter chain.
   * @throws Exception when error occurs.
   */
  @Bean
  public SecurityFilterChain apikeyFilterChain(HttpSecurity httpSecurity) throws Exception {

    ApiAuthKeyFilter apiAuthKeyFilter = new ApiAuthKeyFilter(apiKeyAuthHeaderName);
    apiAuthKeyFilter.setAuthenticationManager(apiAuthKeyManager);

    httpSecurity
        .exceptionHandling()
        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    // Secure end point
    httpSecurity
        .antMatcher(urlContextV1)
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

  /**
   * Sets the security filter chain.
   *
   * @param httpSecurity http security.
   * @return a security build.
   * @throws Exception when error occurs.
   */
  @Bean
  public SecurityFilterChain jwtFilterChain(HttpSecurity httpSecurity) throws Exception {

    ApiAuthKeyFilter apiAuthKeyFilter = new ApiAuthKeyFilter(jwtAuthHeaderName);
    apiAuthKeyFilter.setAuthenticationManager(apiAuthKeyManager);

    httpSecurity
        .exceptionHandling()
        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

    // Secure end point
    httpSecurity
        .antMatcher(urlContextV2)
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
