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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

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

    httpSecurity
        .exceptionHandling()
        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    // Secure end point
    httpSecurity
        .requestMatchers()
        .antMatchers(
            claimInfo, claimMetrics, evidencePdf, fullHealth, healthAssessment, immediatePdf)
        .and()
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
        .requestMatchers()
        .antMatchers(automatedClaim, examOrder)
        .and()
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
