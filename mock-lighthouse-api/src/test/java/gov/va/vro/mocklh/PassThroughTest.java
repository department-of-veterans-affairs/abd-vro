package gov.va.vro.mocklh;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.mocklh.config.LhApiProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("test")
public class PassThroughTest {
  static {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
  }

  @Autowired private LhApiProperties props;

  @Autowired private RestTemplate template;

  @LocalServerPort private int port;

  private static String getPatientCoding(String patientIcn) {
    String patientString = String.format("{\"patient\":\"%s\"}", patientIcn);
    return Base64.getEncoder().encodeToString(patientString.getBytes());
  }

  @SneakyThrows
  private String getCcgAssertion() {
    String assertionUrl = props.getAssertionUrl();
    String clientId = props.getClientId();
    String pemKey = props.getPemKey();
    InputStream inputStream = new ByteArrayInputStream(pemKey.getBytes());
    final PemReader pemReader = new PemReader(new InputStreamReader(inputStream));
    PemObject pemObject = null;
    try {
      pemObject = pemReader.readPemObject();
    } catch (Exception ex) {
    } finally {
      pemReader.close();
    }
    final byte[] content = pemObject.getContent();
    final PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
    final KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
    PrivateKey key = factory.generatePrivate(privKeySpec);

    Date issuedAt = new Date();
    Date expiredOn = new Date(issuedAt.getTime() + 60 * 3 * 1000);
    return Jwts.builder()
        .setAudience(assertionUrl)
        .setIssuer(clientId)
        .setSubject(clientId)
        .setIssuedAt(issuedAt)
        .setExpiration(expiredOn)
        .signWith(SignatureAlgorithm.RS256, key)
        .compact();
  }

  @Test
  void icn1012666073V986297Test() {
    final String icn = "1012666073V986297";
    final String assertion = getCcgAssertion();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    String launchCode = getPatientCoding(icn);
    requestBody.add("grant_type", "client_credentials");
    requestBody.add(
        "client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
    requestBody.add("client_assertion", assertion);
    requestBody.add("launch", launchCode);
    requestBody.add("scope", "launch patient/Observation.read");
    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);

    String tokenUrl = "http://localhost:" + port + "/token";
    ResponseEntity<String> tokenResp = template.postForEntity(tokenUrl, entity, String.class);
    assertNotNull(tokenResp.getBody());
  }
}
