package gov.va.vro.security;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ApiAuthKeyManager implements AuthenticationManager {

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

        String principal = (String) authentication.getPrincipal();

        if (!apiAuthKeys.getKeys().contains(principal)) {
            throw new BadCredentialsException("Invalid API Key.");
        } else {
            authentication.setAuthenticated(true);
            return authentication;
        }
    }
}
