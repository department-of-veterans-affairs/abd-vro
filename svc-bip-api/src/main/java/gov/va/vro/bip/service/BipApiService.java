package gov.va.vro.bip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.model.BipClaimResp;
import gov.va.vro.bip.model.BipPayloadResponse;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import gov.va.vro.bip.model.ClaimStatus;
import gov.va.vro.bip.model.UpdateContentionReq;
import gov.va.vro.bip.model.contentions.GetClaimContentionsResponse;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionRequest;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;

/**
 * BIP claim API service.
 *
 * @author warren @Date 10/31/22
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BipApiService implements IBipApiService {
  static final String CLAIM_DETAILS = "/claims/%s";
  static final String TEMP_STATION_OF_JURISDICTION = "/claims/%s/temporary_station_of_jurisdiction";
  static final String UPDATE_CLAIM_STATUS = "/claims/%s/lifecycle_status";
  static final String CONTENTION = "/claims/%s/contentions";
  static final String SPECIAL_ISSUE_TYPES = "/contentions/special_issue_types";

  static final String HTTPS = "https://";

  static final String JWT_TYPE = "JWT";

  @Qualifier("bipCERestTemplate")
  @NonNull
  final RestTemplate restTemplate;

  final BipApiProps bipApiProps;

  final ObjectMapper mapper = new ObjectMapper();

  @Override
  public BipClaimResp getClaimDetails(long claimId) {
    try {
      log.info("getClaimDetails({}) invoked", claimId);
      String url = HTTPS + bipApiProps.getClaimBaseUrl() + String.format(CLAIM_DETAILS, claimId);
      log.info("call {} to get claim info.", url);
      HttpHeaders headers = getBipHeader();
      HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(headers);
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
      if (bipResponse.getStatusCode() == HttpStatus.OK) {
        BipClaimResp result = mapper.readValue(bipResponse.getBody(), BipClaimResp.class);
        result.statusCode = HttpStatus.OK.value();
        result.statusMessage = HttpStatus.OK.getReasonPhrase();
        return result;
      } else {
        log.error(
            "Failed to get claim details for {}. {} \n{}",
            claimId,
            bipResponse.getStatusCode(),
            bipResponse.getBody());
        throw new BipException(bipResponse.getStatusCode(), bipResponse.getBody());
      }
    } catch (JsonProcessingException e) {
      log.error("json processing error", e);
      throw new BipException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    } catch (HttpStatusCodeException e) {
      log.error("Failed to get claim info for claim ID {}.", claimId, e);
      throw new BipException(e.getStatusCode(), e.getMessage());
    } catch (RestClientException e) {
      log.error("Failed to get claim info for claim ID {}.", claimId, e);
      throw new BipException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  /**
   * Updates claim status.
   *
   * @param claimId claim ID for the claim to be updated.
   * @return an object with status and message.
   * @throws BipException error occurs
   */
  @Override
  public BipUpdateClaimResp setClaimToRfdStatus(long claimId) {
    return updateClaimStatus(claimId, ClaimStatus.RFD);
  }

  @Override
  public BipUpdateClaimResp updateClaimStatus(long claimId, ClaimStatus status) {
    log.info("updateClaimStatus({},{}) invoked.", claimId, status);
    final String description = status.getDescription();
    try {

      String url =
          HTTPS + bipApiProps.getClaimBaseUrl() + String.format(UPDATE_CLAIM_STATUS, claimId);
      log.info("call {} to update claim status to {}.", url, description);

      HttpHeaders headers = getBipHeader();
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("claimLifecycleStatus", description);
      HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(requestBody, headers);
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
      if (bipResponse.getStatusCode() == HttpStatus.OK) {
        return new BipUpdateClaimResp(HttpStatus.OK, bipResponse.getBody());
      } else {
        throw new BipException(bipResponse.getStatusCode(), bipResponse.getBody());
      }
    } catch (RestClientException e) {
      log.error("failed to update status to {} for claim {}.", description, claimId, e);
      throw new BipException(e.getMessage(), e);
    }
  }

  @Override
  public GetClaimContentionsResponse getClaimContentions(long claimId) {
    String url = HTTPS + bipApiProps.getClaimBaseUrl() + String.format(CONTENTION, claimId);

    return makeRequest(url, HttpMethod.GET, GetClaimContentionsResponse.class);
  }

  @Override
  public BipUpdateClaimResp updateClaimContention(long claimId, UpdateContentionReq contention) {
    try {
      String url = HTTPS + bipApiProps.getClaimBaseUrl() + String.format(CONTENTION, claimId);
      log.info("Call {} to update contention for {}.", url, claimId);
      HttpHeaders headers = getBipHeader();
      HttpEntity<UpdateContentionReq> httpEntity = new HttpEntity<>(contention, headers);
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
      if (bipResponse.getStatusCode() == HttpStatus.OK) {
        return new BipUpdateClaimResp(HttpStatus.OK, bipResponse.getBody());
      } else {
        throw new BipException(bipResponse.getStatusCode(), bipResponse.getBody());
      }
    } catch (RestClientException e) {
      log.error("failed to updateClaimContentions for claim {}.", claimId, e);
      throw new BipException(e.getMessage(), e);
    }
  }

  @Override
  public PutTempStationOfJurisdictionResponse putTempStationOfJurisdiction(
      PutTempStationOfJurisdictionRequest request) {
    long claimId = request.getClaimId();
    String url =
        HTTPS
            + bipApiProps.getClaimBaseUrl()
            + String.format(TEMP_STATION_OF_JURISDICTION, claimId);

    String tsoj = request.getTempStationOfJurisdiction();
    Map<String, String> requestBody = Map.of("tempStationOfJurisdiction", tsoj);

    return makeRequest(
        url, HttpMethod.PUT, requestBody, PutTempStationOfJurisdictionResponse.class);
  }

  @SuppressWarnings("unchecked")
  private <T extends BipPayloadResponse> T makeRequest(
      String url, HttpMethod method, Map<String, String> requestBody, Class<T> expectedResponse) {
    try {
      HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(requestBody, getBipHeader());
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, method, httpEntity, String.class);
      log.info(
          "event=responseReceived url={} method={} status={}",
          url,
          method,
          bipResponse.getStatusCode().value());
      return (T)
          mapper.readValue(bipResponse.getBody(), expectedResponse).toBuilder()
              .statusCode(bipResponse.getStatusCode().value())
              .statusMessage(
                  HttpStatus.valueOf(bipResponse.getStatusCode().value()).getReasonPhrase())
              .build();
    } catch (HttpStatusCodeException e) {
      throw e;
    } catch (Exception e) {
      log.error(
          "event=requestFailed url={} method={} status={} error={}",
          url,
          method,
          HttpStatus.INTERNAL_SERVER_ERROR.value(),
          e.getMessage());
      throw new BipException(e.getMessage(), e);
    }
  }

  private <T extends BipPayloadResponse> T makeRequest(
      String url, HttpMethod method, Class<T> expectedResponse) {
    return makeRequest(url, method, null, expectedResponse);
  }

  /**
   * Verifies that the BIP Api responds to a request. Calls the special_issue_types URL and confirms
   * the response status is OK and body is not empty
   *
   * @return true if the API responds with OK status and response.
   */
  public boolean isApiFunctioning() {
    String url = HTTPS + bipApiProps.getClaimBaseUrl() + SPECIAL_ISSUE_TYPES;
    log.info("Call {} to get special_issue_types", url);

    HttpHeaders headers = getBipHeader();
    HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

    try {
      ResponseEntity<String> response =
          restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
      boolean statusIsOK = response.getStatusCode() == HttpStatus.OK;
      boolean responseIsNotEmpty = (response.getBody() != null && !response.getBody().isEmpty());
      return statusIsOK && responseIsNotEmpty;
    } catch (HttpStatusCodeException e) {
      log.error("API {} failed", url, e);
      return false;
    }
  }

  HttpHeaders getBipHeader() throws BipException {
    try {
      HttpHeaders bipHttpHeaders = new HttpHeaders();
      bipHttpHeaders.setContentType(MediaType.APPLICATION_JSON);

      String jwt = createJwt();
      bipHttpHeaders.add("Authorization", "Bearer " + jwt);
      return bipHttpHeaders;
    } catch (Exception e) {
      log.error("Failed to build BIP HTTP Headers.", e);
      throw new BipException(e.getMessage(), e);
    }
  }

  String createJwt() throws BipException {
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
