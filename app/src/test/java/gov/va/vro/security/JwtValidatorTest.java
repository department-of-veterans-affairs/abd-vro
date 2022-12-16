package gov.va.vro.security;

import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.vro.config.LhApiProps;
import org.junit.jupiter.api.Test;

class JwtValidatorTest {

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

  @Test
  void validateTokenUsingLh() {
    var url = "https://placeholder.com/";
    var props = new LhApiProps(url, url, "", "YES");
    var validator = new JwtValidator(props);
    assertThrows(InvalidTokenException.class, () -> validator.validateTokenUsingLh(sampleJwt));
  }
}
