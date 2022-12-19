package gov.va.vro.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.va.vro.controller.BaseControllerTest;
import gov.va.vro.security.ApiAuthKeyManager;
import gov.va.vro.security.JwtValidator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MasAuthTest extends BaseControllerTest {
  @Mock Authentication authentication;

  @InjectMocks @Autowired JwtValidator jwtValidator;
  @InjectMocks @Autowired ApiAuthKeyManager apiAuthKeyManager;

  @Test
  void testNotAuthenticated() {
    assertFalse(apiAuthKeyManager.authenticate(authentication).isAuthenticated());
  }

  @Test
  void testAuthenticateHeader() {

    MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
    httpServletRequest.setRequestURI("/v2/automatedClaim");
    apiAuthKeyManager.setHttpServletRequest(httpServletRequest);
    String sampleJwt =
        """
                   eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImMwOTI5NTJlLTM4ZDYtNDNjNi0\
                   5MzBlLWZmOTNiYTUxYjA4ZiJ9.eyJleHAiOjk5OTk5OTk5OTksImlhdCI6MTY0MTA2Nzk0O\
                   SwianRpIjoiNzEwOTAyMGEtMzlkOS00MWE4LThlNzgtNTllZjAwYTlkNDJlIiwiaXNzIjoi\
                   aHR0cHM6Ly9zYW5kYm94LWFwaS52YS5nb3YvaW50ZXJuYWwvYXV0aC92Mi92YWxpZGF0aW9\
                   uIiwiYXVkIjoibWFzX2RldiIsInN1YiI6IjhjNDkyY2NmLTk0OGYtNDQ1Zi05NmY4LTMxZT\
                   dmODU5MDlkMiIsInR5cCI6IkJlYXJlciIsImF6cCI6Im1hc19kZXYiLCJzY29wZSI6Im9wZ\
                   W5pZCB2cm9fbWFzIiwiY2xpZW50SWQiOiJtYXNfZGV2In0.Qb41CR1JIGGRlryi-XVtqyeN\
                   W73cU1YeBVqs9Bps3TA""";

    String authorizationHdr = "Bearer " + sampleJwt;
    when(authentication.getPrincipal()).thenReturn(authorizationHdr);
    apiAuthKeyManager.authenticate(authentication);
    verify(authentication).setAuthenticated(true);
  }
}
