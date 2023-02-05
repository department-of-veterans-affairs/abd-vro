package org.openapitools.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
public class SecurityConfig {
  // private final JwtRequestFilter filter;

  // @Qualifier("delegatedAuthenticationEntryPoint")
  // private final AuthenticationEntryPoint authEntryPoint;

  /**
   * Spring security to validate JWT.
   *
   * @param http HttpSecurity
   * @return SecurityFilterChain
   * @throws Exception Authentication failuure
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    // http.authorizeRequests().antMatchers("/received-files/*").permitAll();
    // http.authorizeRequests().antMatchers("/actuator/health").permitAll();
    // http.authorizeRequests().anyRequest().authenticated();
    http.authorizeRequests().anyRequest().permitAll();
    // http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    // http.exceptionHandling().authenticationEntryPoint(authEntryPoint);
    return http.build();
  }
}
