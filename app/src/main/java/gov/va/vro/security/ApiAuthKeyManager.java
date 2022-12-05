package gov.va.vro.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Order(1)
public class ApiAuthKeyManager implements AuthenticationManager {
  private ApiAuthKeys apiAuthKeys;

  private JwtValidator jwtValidator;

  public ApiAuthKeys getApiAuthKeys() {
    return apiAuthKeys;
  }

  @Autowired
  public void setApiAuthKeys(ApiAuthKeys apiAuthKeys) {
    this.apiAuthKeys = apiAuthKeys;
  }

  public JwtValidator getJwtValidator() {
    return jwtValidator;
  }

  @Autowired
  public void setJwtValidator(JwtValidator jwtValidator) {
    this.jwtValidator = jwtValidator;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    String authorizationHdr = (String) authentication.getPrincipal();
    log.info("Header Information : " + authorizationHdr);
    authentication.setAuthenticated(false); // Default

    if (authorizationHdr == null) {
      return authentication;
    }

    if (authorizationHdr.startsWith("Bearer ")) {
      // Validate JWT token
      try {
        String jwtToken = jwtValidator.subStringBearer(authorizationHdr);
        DecodedJWT decodedJWT = jwtValidator.decodeToken(jwtToken);
        jwtValidator.verifyTokenHeader(decodedJWT);
        jwtValidator.verifyPayload(decodedJWT);
        jwtValidator.validateTokenUsingLH(jwtToken);
        authentication.setAuthenticated(true);
      } catch (InvalidTokenException invalidTokenException) {
        log.info("Tried to access with invalid JWT Token, {}", invalidTokenException.getMessage());
        throw new BadCredentialsException("Invalid JWT Token.");
      }
    } else {
      // Validate API key
      if (!apiAuthKeys.getKeys().contains(authorizationHdr)) {
        log.info("Tried to access with invalid key, {}", authorizationHdr);
        throw new BadCredentialsException("Invalid API Key.");
      } else {
        authentication.setAuthenticated(true); //
      }
    }
    return authentication;
  }
}
