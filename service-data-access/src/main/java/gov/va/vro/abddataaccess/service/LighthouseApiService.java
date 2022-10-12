package gov.va.vro.abddataaccess.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.abddataaccess.config.properties.LighthouseProperties;
import gov.va.vro.abddataaccess.exception.LightHouseException;
import gov.va.vro.abddataaccess.model.LighthouseTokenMessage;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * Lighthouse FHIR API access service.
 *
 * @author WarrenLin
 */
@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class LighthouseApiService {

  static {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
  }

  private static final String PATIENT_CODING = "{\"patient\":\"%s\"}";

  @Autowired private LighthouseProperties lhProps;

  @Autowired private RestTemplate restTemplate;

  /**
   * Creates a bear token to access the Lighthouse FHIR API endpoint for the given patient.
   *
   * @param domain the domain data to be retrieved from the Lighthouse FHIR API.
   * @param patientIcn the patient ICN.
   * @return a token.
   * @throws LightHouseException when error occurs.
   */
  public String getLighthouseToken(AbdDomain domain, String patientIcn) throws LightHouseException {
    String scope = domain.getScope();
    LighthouseTokenMessage tokenMessage = getToken(patientIcn, scope);
    return "Bearer " + tokenMessage.getAccessToken();
  }

  /**
   * Gets patient ICN coded based on Lighthouse API requirement.
   *
   * @param patientIcn Patient ICN
   * @return a string.
   */
  private String getPatientCoding(String patientIcn) {
    String patientString = String.format(PATIENT_CODING, patientIcn);
    return Base64.getEncoder().encodeToString(patientString.getBytes());
  }

  private String getCcgAssertion(String assertionUrl, String clientId) throws LightHouseException {
    try {
      InputStream inputStream = new ByteArrayInputStream(lhProps.getPemkey().getBytes());
      final PemReader pemReader = new PemReader(new InputStreamReader(inputStream));
      PemObject pemObject;
      try {
        pemObject = pemReader.readPemObject();
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
    } catch (IOException
        | NoSuchAlgorithmException
        | NoSuchProviderException
        | InvalidKeySpecException e) {
      log.error("Failed to create assertion for VA Lighthouse API. {}", e.getMessage(), e);
      throw new LightHouseException("Failed to create signing key for VA Lighthouse API.", e);
    } catch (NullPointerException e) {
      log.error("Failed to find a valid key for VA Lighthouse API. {}", e.getMessage(), e);
      throw new LightHouseException("Cannot find a valid key for Lighthouse access.", e);
    }
  }

  private LighthouseTokenMessage getToken(String patientIcn, String scope)
      throws LightHouseException {
    String assertion = getCcgAssertion(lhProps.getAssertionurl(), lhProps.getClientId());
    String result = getToken(assertion, patientIcn, scope);
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(result, LighthouseTokenMessage.class);
    } catch (IOException e) {
      log.error("Failed to parse lighthouse token message.", e);
      throw new LightHouseException("Failed to parse lighthouse token message.", e);
    }
  }

  private String getToken(String assertion, String patientIcn, String scope)
      throws LightHouseException {
    return getToken(lhProps.getTokenurl(), assertion, patientIcn, scope);
  }

  private String getToken(String tokenUtl, String assertion, String patientIcn, String scope)
      throws LightHouseException {
    log.info("get httpEntity for token. tokenurl={}\n, scope={}", tokenUtl, scope);
    HttpEntity<MultiValueMap<String, String>> httpEntity =
        getLighthouseTokenRequestEntity(assertion, patientIcn, scope);
    try {
      ResponseEntity<String> tokenResp =
          restTemplate.postForEntity(tokenUtl, httpEntity, String.class);

      return tokenResp.getBody();
    } catch (RestClientException e) {
      log.error("Failed to get Lighthouse token.", e);
      throw new LightHouseException("Failed to get Lighthouse token.", e);
    }
  }

  @NotNull
  private HttpEntity<MultiValueMap<String, String>> getLighthouseTokenRequestEntity(
      String assertion, String patientIcn, String scope) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    String launchCode = getPatientCoding(patientIcn);
    requestBody.add("grant_type", "client_credentials");
    requestBody.add(
        "client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
    requestBody.add("client_assertion", assertion);
    requestBody.add("launch", launchCode);
    requestBody.add("scope", scope);
    return new HttpEntity<>(requestBody, headers);
  }
}
