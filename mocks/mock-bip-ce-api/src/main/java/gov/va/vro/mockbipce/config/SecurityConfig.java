package gov.va.vro.mockbipce.config;

import gov.va.vro.mockshared.rest.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtRequestFilter filter;

  @Qualifier("delegatedAuthenticationEntryPoint")
  private final AuthenticationEntryPoint authEntryPoint;

  /**
   * Spring security to validate JWT.
   *
   * @param http HttpSecurity
   * @return SecurityFilterChain
   * @throws Exception Authentication failuure
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(CsrfConfigurer::disable);
    http.sessionManagement(
        (securitySessionManagementConfigurer) ->
            securitySessionManagementConfigurer.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));
    http.authorizeHttpRequests(
        (authorizeHttpRequests) -> {
          authorizeHttpRequests.requestMatchers(new AntPathRequestMatcher("/received-files/*")).permitAll();
          authorizeHttpRequests.requestMatchers(new AntPathRequestMatcher("/actuator/health")).permitAll();
          authorizeHttpRequests.anyRequest().authenticated();
        });
    http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    http.exceptionHandling(
        (exceptionHandlingConfigurer) ->
            exceptionHandlingConfigurer.authenticationEntryPoint(authEntryPoint));
    return http.build();
  }
}
