package gov.va.vro.service.provider.mas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasAuthToken {

  private static final String CLIENT_REGISTRATION_ID = "masAuthProvider";
  private static final String PRINCIPAL_NAME = "MAS Service";

  // Inject the OAuth authorized client service and authorized client manager
  // from the MasOauth2Config class (check the app folder).
  private final AuthorizedClientServiceOAuth2AuthorizedClientManager
      authorizedClientServiceAndManager;

  public final AuthorizedClientServiceOAuth2AuthorizedClientManager
      getAuthorizedClientServiceAndManager() {
    return authorizedClientServiceAndManager;
  }

  // Retrieve the authorized JWT from MAS.

  /**
   * Gets the MAS API token.
   *
   * @return token
   */
  public OAuth2AccessToken getMasApiAuthToken() {

    // Build an OAuth2 request for the MAS Auth provider
    OAuth2AuthorizeRequest authorizeRequest =
        OAuth2AuthorizeRequest.withClientRegistrationId(CLIENT_REGISTRATION_ID)
            .principal(PRINCIPAL_NAME)
            .build();

    // Perform the actual authorization request using the authorized client service and authorized
    // client manager. This is where the JWT is retrieved from the MAS Auth servers.
    OAuth2AuthorizedClient authorizedClient =
        authorizedClientServiceAndManager.authorize(authorizeRequest);

    // Get the token from the authorized client object.
    return Objects.requireNonNull(authorizedClient).getAccessToken();
  }
}
