package gov.va.vro.security;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@NoArgsConstructor
public class ApiAuthKeyManager implements AuthenticationManager {

  @Autowired
  ApiAuthKeys apiAuthKeys;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    String principal = (String) authentication.getPrincipal();
    System.out.println("Rajesh api key validated outside : " + principal);
    for (String keys : apiAuthKeys.getKeys()) {
      System.out.println("Rajesh api key validated : " + keys);
    }
//    authentication.setAuthenticated(false);
//    return authentication;

        if (!apiAuthKeys.getKeys().contains(principal)) {
          throw new BadCredentialsException("Invalid API Key.");
        } else {
          authentication.setAuthenticated(true);
          return authentication;
        }
  }
}
