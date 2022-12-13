package gov.va.vro.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import gov.va.vro.controller.BaseControllerTest;
import gov.va.vro.security.ApiAuthKeyManager;
import gov.va.vro.security.ApiAuthKeys;
import gov.va.vro.security.InvalidTokenException;
import gov.va.vro.security.JwtValidator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MasAuthTest extends BaseControllerTest {

  @Mock LhApiProps lhApiPropsMock;

  @Mock ApiAuthKeys apiAuthKeysMock;

  @InjectMocks JwtValidator jwtValidatorMock;

  @Mock Authentication authentication;

  @InjectMocks ApiAuthKeyManager apiAuthKeyManagerMock;

  @Test
  void testAuthenticateHeader() {
    assertFalse(apiAuthKeyManagerMock.authenticate(authentication).isAuthenticated());
    String sampleJWT =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImMwOTI5NTJlLTM4ZDYtNDNjNi05MzBlLWZmOTNiYTUxYjA4ZiJ9.eyJleHAiOjk5OTk5OTk5OTksImlhdCI6MTY0MTA2Nzk0OSwianRpIjoiNzEwOTAyMGEtMzlkOS00MWE4LThlNzgtNTllZjAwYTlkNDJlIiwiaXNzIjoiaHR0cHM6Ly9zYW5kYm94LWFwaS52YS5nb3YvaW50ZXJuYWwvYXV0aC92Mi92YWxpZGF0aW9uIiwiYXVkIjoibWFzX2RldiIsInN1YiI6IjhjNDkyY2NmLTk0OGYtNDQ1Zi05NmY4LTMxZTdmODU5MDlkMiIsInR5cCI6IkJlYXJlciIsImF6cCI6Im1hc19kZXYiLCJzY29wZSI6Im9wZW5pZCB2cm9fbWFzIiwiY2xpZW50SWQiOiJtYXNfZGV2In0.Qb41CR1JIGGRlryi-XVtqyeNW73cU1YeBVqs9Bps3TA";

    String authorizationHdr = "Bearer " + sampleJWT;
    when(authorizationHdr.startsWith("Bearer ")).thenReturn(Boolean.TRUE);
    when(lhApiPropsMock.getValidateToken()).thenReturn("yes");
    assertTrue(apiAuthKeyManagerMock.authenticate(authentication).isAuthenticated());
  }

  @Test
  void testAuthenticateLhAPI() {
    assertFalse(apiAuthKeyManagerMock.authenticate(authentication).isAuthenticated());
    String sampleJWT =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImMwOTI5NTJlLTM4ZDYtNDNjNi05MzBlLWZmOTNiYTUxYjA4ZiJ9.eyJleHAiOjk5OTk5OTk5OTksImlhdCI6MTY0MTA2Nzk0OSwianRpIjoiNzEwOTAyMGEtMzlkOS00MWE4LThlNzgtNTllZjAwYTlkNDJlIiwiaXNzIjoiaHR0cHM6Ly9zYW5kYm94LWFwaS52YS5nb3YvaW50ZXJuYWwvYXV0aC92Mi92YWxpZGF0aW9uIiwiYXVkIjoibWFzX2RldiIsInN1YiI6IjhjNDkyY2NmLTk0OGYtNDQ1Zi05NmY4LTMxZTdmODU5MDlkMiIsInR5cCI6IkJlYXJlciIsImF6cCI6Im1hc19kZXYiLCJzY29wZSI6Im9wZW5pZCB2cm9fbWFzIiwiY2xpZW50SWQiOiJtYXNfZGV2In0.Qb41CR1JIGGRlryi-XVtqyeNW73cU1YeBVqs9Bps3TA";

    String authorizationHdr = "Bearer " + sampleJWT;
    when(authorizationHdr.startsWith("Bearer ")).thenReturn(Boolean.TRUE);
    when(lhApiPropsMock.getValidateToken()).thenReturn("yes");
    when(lhApiPropsMock.getTokenValidatorURL()).thenReturn("https://placeholder.com/");

    Throwable exception =
        assertThrows(
            InvalidTokenException.class, () -> jwtValidatorMock.validateTokenUsingLH(sampleJWT));
    assertEquals("Could not validate token against LightHouse API", exception.getMessage());
  }
}
