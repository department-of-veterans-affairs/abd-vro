package gov.va.vro.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ApiAuthKeyManager implements AuthenticationManager {

  private final List<String> apiAuthKeys;

  public ApiAuthKeyManager(@Value("${apiauthkeys}") List<String> apiAuthKeys) {
    this.apiAuthKeys = apiAuthKeys;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    String principal = (String) authentication.getPrincipal();

    if (!apiAuthKeys.contains(principal)) {
      throw new BadCredentialsException("Invalid API Key.");
    } else {
      authentication.setAuthenticated(true);
      return authentication;
    }
  }
}
