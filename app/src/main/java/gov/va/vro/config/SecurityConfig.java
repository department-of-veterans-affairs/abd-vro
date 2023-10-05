package gov.va.vro.config;

import gov.va.vro.security.ApiAuthKeyFilter;
import gov.va.vro.security.ApiAuthKeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class SecurityConfig {

  @Value("${apiauth.hdr-key-name-v1}")
  private String apiKeyAuthHeaderName;

  @Value("${apiauth.immediate-pdf}")
  private String immediatePdf;

  @Value("${apiauth.evidence-pdf}")
  private String evidencePdf;

  @Value("${apiauth.full-health-assessment}")
  private String fullHealth;

  @Value("${apiauth.health-assessment}")
  private String healthAssessment;

  @Value("${apiauth.automated-claim}")
  private String automatedClaim;

  @Value("${apiauth.exam-order}")
  private String examOrder;

  @Value("${apiauth.claim-metrics}")
  private String claimMetrics;

  @Value("${apiauth.claim-info}")
  private String claimInfo;

  @Value("${apiauth.hdr-key-name-v2}")
  private String jwtAuthHeaderName;

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

    httpSecurity.exceptionHandling(
        (httpSecurityExceptionHandlingConfigurer ->
            httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))));
    // Secure end point
    httpSecurity
        .authorizeHttpRequests(
            (authz) -> {
              authz
                  .requestMatchers(new AntPathRequestMatcher(claimInfo))
                  .permitAll()
                  .requestMatchers(new AntPathRequestMatcher(claimMetrics))
                  .permitAll()
                  .requestMatchers(new AntPathRequestMatcher(evidencePdf))
                  .permitAll()
                  .requestMatchers(new AntPathRequestMatcher(fullHealth))
                  .permitAll()
                  .requestMatchers(new AntPathRequestMatcher(healthAssessment))
                  .permitAll()
                  .requestMatchers(new AntPathRequestMatcher(immediatePdf))
                  .permitAll()
                  .anyRequest()
                  .authenticated();
            })
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            httpSecuritySessionManagementConfigurer ->
                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
        .addFilter(apiAuthKeyFilter);
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

    httpSecurity.exceptionHandling(
        (httpSecurityExceptionHandlingConfigurer ->
            httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))));
    // Secure end point
    httpSecurity
        .authorizeHttpRequests(
            (authz) ->
                authz
                    .requestMatchers(new AntPathRequestMatcher(automatedClaim))
                    .permitAll()
                    .requestMatchers(new AntPathRequestMatcher(examOrder))
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            httpSecuritySessionManagementConfigurer ->
                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
        .addFilter(apiAuthKeyFilter);
    return httpSecurity.build();
  }
}
