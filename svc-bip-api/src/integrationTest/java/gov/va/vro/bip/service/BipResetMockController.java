package gov.va.vro.bip.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;

@Service
@RequiredArgsConstructor
@Slf4j
public class BipResetMockController {

  private static final String UPDATES = "/updates/%s";
  private static final String HTTPS = "https://";
  private static final String JWT_TYPE = "JWT";

  @Qualifier("bipCERestTemplate")
  final RestTemplate restTemplate;

  final BipApiProps bipApiProps;

  public void resetClaim(long claimId) {
    String url = HTTPS + bipApiProps.getClaimBaseUrl() + String.format(UPDATES, claimId);

    try {
      HttpEntity<Object> httpEntity = new HttpEntity<>(null, getBipHeader());
      restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, String.class);
    } catch (HttpStatusCodeException e) {
      log.info(
          "event=responseReceived url={} status={} statusMessage={}",
          url,
          e.getStatusCode(),
          ((HttpStatus) e.getStatusCode()).name());
      throw e;
    } catch (Exception e) {
      log.error(
          "event=requestFailed url={} method={} status={} error={}",
          url,
          HttpMethod.DELETE,
          HttpStatus.INTERNAL_SERVER_ERROR.value(),
          e.getMessage());
      throw new MockBipException(e.getMessage(), e);
    }
  }

  HttpHeaders getBipHeader() throws BipException {
    HttpHeaders bipHttpHeaders = new HttpHeaders();
    bipHttpHeaders.setContentType(MediaType.APPLICATION_JSON);

    String jwt = createJwt();
    bipHttpHeaders.add("Authorization", "Bearer " + jwt);
    return bipHttpHeaders;
  }

  String createJwt() {
    Claims claims = bipApiProps.toCommonJwtClaims();
    Map<String, Object> headerType = new HashMap<>();
    headerType.put("typ", JWT_TYPE);

    ClaimsBuilder claimsBuilder =
        Jwts.claims().add(claims).add("iss", bipApiProps.getClaimIssuer());
    claims = claimsBuilder.build();
    byte[] signSecretBytes = bipApiProps.getClaimSecret().getBytes(StandardCharsets.UTF_8);
    Key signingKey = new SecretKeySpec(signSecretBytes, "HmacSHA256");
    return Jwts.builder()
        .subject("Claim")
        .issuedAt(Calendar.getInstance().getTime())
        .expiration(claims.getExpiration())
        .claims(claims)
        .signWith(signingKey)
        .header()
        .add(headerType)
        .and()
        .compact();
  }
}
