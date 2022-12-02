package gov.va.vro.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import gov.va.vro.config.LhApiProps;
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
  private static final String VALIDATE_TOKEN = "yes";
  private LhApiProps lhApiProps;
  private ApiAuthKeys apiAuthKeys;

  public ApiAuthKeys getApiAuthKeys() {
    return apiAuthKeys;
  }

  @Autowired
  public void setApiAuthKeys(ApiAuthKeys apiAuthKeys) {
    this.apiAuthKeys = apiAuthKeys;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    String authorizationHdr = (String) authentication.getPrincipal();
    log.info("Header Information : " + authorizationHdr);
    authentication.setAuthenticated(false); //Default

    if (authorizationHdr == null) {
      return authentication;
     }

    if (authorizationHdr.startsWith("Bearer ")) {
      // Validate JWT token
      try {
        JwtValidator jwtValidator = new JwtValidator(authorizationHdr);
        String jwtToken = jwtValidator.subStringBearer(authorizationHdr);
        DecodedJWT decodedJWT = jwtValidator.decodeToken(jwtToken);
        jwtValidator.verifyTokenHeader(decodedJWT);
        jwtValidator.verifyPayload(decodedJWT);
        if (lhApiProps.getValidateToken().toLowerCase() == VALIDATE_TOKEN) {
           if (jwtValidator.validateTokenUsingLH(jwtToken,
                   lhApiProps.getApiKey(),
                   lhApiProps.getTokenValidatorURL(),
                   lhApiProps.getVroAudURL())) {
             authentication.setAuthenticated(true);
           }
        } else {
          authentication.setAuthenticated(true);
        }
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
