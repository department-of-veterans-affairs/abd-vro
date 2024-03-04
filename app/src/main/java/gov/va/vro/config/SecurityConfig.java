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

  private final String actuatorUrls = "/actuator/**";

  private final String v3Urls = "/v3/**";

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
        .securityMatchers(
            (matchers) ->
                matchers.requestMatchers(
                    new AntPathRequestMatcher(claimInfo),
                    new AntPathRequestMatcher(claimMetrics),
                    new AntPathRequestMatcher(evidencePdf),
                    new AntPathRequestMatcher(fullHealth),
                    new AntPathRequestMatcher(healthAssessment),
                    new AntPathRequestMatcher(immediatePdf)))
        .authorizeHttpRequests(
            (authz) -> {
              authz
                  .requestMatchers(
                      new AntPathRequestMatcher(claimInfo),
                      new AntPathRequestMatcher(claimMetrics),
                      new AntPathRequestMatcher(evidencePdf),
                      new AntPathRequestMatcher(fullHealth),
                      new AntPathRequestMatcher(healthAssessment),
                      new AntPathRequestMatcher(immediatePdf),
                      new AntPathRequestMatcher(actuatorUrls),
                      new AntPathRequestMatcher(v3Urls))
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
        .securityMatchers(
            (matchers) ->
                matchers.requestMatchers(
                    new AntPathRequestMatcher(automatedClaim),
                    new AntPathRequestMatcher(examOrder)))
        .authorizeHttpRequests(
            (authz) ->
                authz
                    .requestMatchers(
                        new AntPathRequestMatcher(automatedClaim),
                        new AntPathRequestMatcher(examOrder),
                        new AntPathRequestMatcher(actuatorUrls),
                        new AntPathRequestMatcher(v3Urls))
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
