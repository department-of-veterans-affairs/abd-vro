package gov.va.vro.service.provider.mas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.Objects;

// @Getter
// @Component("masAuthToken")
public class MasAuthToken {

  private static final String CLIENT_REGISTRATION_ID = "masAuthProvider";
  private static final String PRINCIPAL_NAME = "MAS Service";

  // Inject the OAuth authorized client service and authorized client manager
  // from the MasOauth2Config class (check the app folder)
  @Autowired
  private AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager;

  // Retrieve the authorized JWT from MAS
  public OAuth2AccessToken getMasAuthToken() {

    // Build an OAuth2 request for the MAS Auth provider
    OAuth2AuthorizeRequest authorizeRequest =
        OAuth2AuthorizeRequest.withClientRegistrationId(CLIENT_REGISTRATION_ID)
            .principal(PRINCIPAL_NAME)
            .build();

    // Perform the actual authorization request using the authorized client service and authorized
    // client
    // manager. This is where the JWT is retrieved from the MAS Auth servers.
    OAuth2AuthorizedClient authorizedClient =
        this.authorizedClientServiceAndManager.authorize(authorizeRequest);

    // Get the token from the authorized client object
    OAuth2AccessToken accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();

    return accessToken;
  }
}
