package gov.va.vro.mocklh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mocklh.config.LhApiProperties;
import gov.va.vro.mocklh.model.LhToken;
import gov.va.vro.mocklh.util.TestSpec;
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
import org.springframework.http.HttpMethod;
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
import java.util.Collections;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("test")
public class ApplicationTest {
  private static final String PLACE_HOLDER = "place-holder";

  static {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
  }

  @Autowired private LhApiProperties props;

  @Autowired private RestTemplate template;

  @Autowired private ObjectMapper mapper;

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

  private MultiValueMap<String, String> newRequestBodyStaticPiece() {
    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

    requestBody.add("grant_type", "client_credentials");
    requestBody.add(
        "client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");

    String assertion = getCcgAssertion();
    requestBody.add("client_assertion", assertion);

    return requestBody;
  }

  private MultiValueMap<String, String> newRequestBody(TestSpec spec) {
    MultiValueMap<String, String> requestBody = newRequestBodyStaticPiece();

    String launchCode = getPatientCoding(spec.getIcn());
    requestBody.add("launch", launchCode);

    requestBody.add("scope", spec.getScope());

    return requestBody;
  }

  private HttpEntity<MultiValueMap<String, String>> getTokenEntity(TestSpec spec) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> requestBody = newRequestBody(spec);

    return new HttpEntity<>(requestBody, headers);
  }

  private LhToken getToken(TestSpec spec) {
    HttpEntity<MultiValueMap<String, String>> entity = getTokenEntity(spec);

    String tokenUrl = props.getTokenUrl();
    ResponseEntity<LhToken> tokenResponse = template.postForEntity(tokenUrl, entity, LhToken.class);
    LhToken token = tokenResponse.getBody();
    return token;
  }

  @SneakyThrows
  private JsonNode getBundle(TestSpec spec) {
    LhToken token = getToken(spec);
    assertNotNull(token.getAccessToken());

    String url = spec.getUrl(port);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token.getAccessToken());
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, entity, String.class);
    return mapper.readTree(response.getBody());
  }

  private void verifyBundle(JsonNode bundle, int count, int entryCount) {
    assertNotNull(bundle);

    JsonNode total = bundle.get("total");
    assertTrue(total.isInt());
    assertEquals(count, total.asInt());

    JsonNode entry = bundle.get("entry");
    assertNotNull(entry);
    assertTrue(entry.isArray());
    assertEquals(entryCount, entry.size());
  }

  private void verifyBundle(JsonNode bundle, int count) {
    verifyBundle(bundle, count, count);
  }

  private boolean checkRunnable() {
    if (PLACE_HOLDER.equals(props.getClientId())) {
      return false;
    }
    if (PLACE_HOLDER.equals(props.getPemKey())) {
      return false;
    }
    return true;
  }

  @SneakyThrows
  @Test
  void icn1012666073V986297BloodPressureTest() {
    if (!checkRunnable()) {
      return; // No LH environment variable. Bail out.
    }

    TestSpec spec =
        TestSpec.builder()
            .icn("1012666073V986297")
            .resourceType("Observation")
            .code("85354-9")
            .build();

    JsonNode bundle = getBundle(spec);
    verifyBundle(bundle, 8);
  }

  @SneakyThrows
  @Test
  void icn9000682ConditionTest() {
    if (!checkRunnable()) {
      return; // No LH environment variable. Bail out.
    }

    TestSpec spec = TestSpec.builder().icn("9000682").resourceType("Condition").build();

    JsonNode bundle = getBundle(spec);
    verifyBundle(bundle, 38);
  }

  @SneakyThrows
  @Test
  void icn9000682MedicationRequestTest() {
    if (!checkRunnable()) {
      return; // No LH environment variable. Bail out.
    }

    TestSpec spec = TestSpec.builder().icn("9000682").resourceType("MedicationRequest").build();

    JsonNode bundle = getBundle(spec);
    verifyBundle(bundle, 11, 8);
  }

  @SneakyThrows
  @Test
  void icnMock1012666073V986297BloodPressureTest() {
    if (!checkRunnable()) {
      return; // No LH environment variable. Bail out.
    }

    TestSpec spec =
        TestSpec.builder()
            .icn("mock1012666073V986297")
            .resourceType("Observation")
            .code("85354-9")
            .build();

    JsonNode bundle = getBundle(spec);
    verifyBundle(bundle, 8);
  }

  @SneakyThrows
  @Test
  void icnMock1012666073V986297MedicationRequestTest() {
    if (!checkRunnable()) {
      return; // No LH environment variable. Bail out.
    }

    TestSpec spec =
        TestSpec.builder().icn("mock1012666073V986297").resourceType("MedicationRequest").build();

    JsonNode bundle = getBundle(spec);
    verifyBundle(bundle, 10);
  }

  @SneakyThrows
  @Test
  void icnMock1012666073V986297ConditionTest() {
    if (!checkRunnable()) {
      return; // No LH environment variable. Bail out.
    }

    TestSpec spec =
        TestSpec.builder().icn("mock1012666073V986297").resourceType("Condition").build();

    JsonNode bundle = getBundle(spec);
    verifyBundle(bundle, 3);
  }
}
