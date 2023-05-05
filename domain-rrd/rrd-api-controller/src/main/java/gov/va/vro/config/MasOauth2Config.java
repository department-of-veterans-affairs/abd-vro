package gov.va.vro.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class MasOauth2Config {
  // Created the MAS Authorization Provider Client Registration.
  @Bean
  ClientRegistration masAuthProviderClientRegistration(
      @Value("${spring.security.oauth2.client.provider.mas.uri}") String tokenUri,
      @Value("${spring.security.oauth2.client.registration.mas.client-id}") String clientId,
      @Value("${spring.security.oauth2.client.registration.mas.client-secret}") String clSecret,
      @Value("${spring.security.oauth2.client.registration.mas.scope}") String scope,
      @Value("${spring.security.oauth2.client.registration.mas.grant-type}") String grantType) {
    return ClientRegistration.withRegistrationId("masAuthProvider")
        .tokenUri(tokenUri)
        .clientId(clientId)
        .clientSecret(clSecret)
        .scope(scope)
        .authorizationGrantType(new AuthorizationGrantType(grantType))
        .build();
  }

  // Create the client registration repository.
  @Bean
  public ClientRegistrationRepository clientRegistrationRepository(
      ClientRegistration masAuthProviderClientRegistration) {
    return new InMemoryClientRegistrationRepository(masAuthProviderClientRegistration);
  }

  // Create the authorized client service.
  @Bean
  public OAuth2AuthorizedClientService auth2AuthorizedClientService(
      ClientRegistrationRepository clientRegistrationRepository) {
    return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
  }

  /**
   * Create the authorized client manager and service manager using the beans created and
   * configured. above
   */
  @Bean
  public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientService authorizedClientService) {

    OAuth2AuthorizedClientProvider authorizedClientProvider =
        OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();

    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
        new AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientService);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    return authorizedClientManager;
  }

  @Bean
  @Primary
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
