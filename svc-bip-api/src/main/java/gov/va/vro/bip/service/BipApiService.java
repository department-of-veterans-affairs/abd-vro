package gov.va.vro.bip.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.model.BipPayloadResponse;
import gov.va.vro.bip.model.cancel.CancelClaimRequest;
import gov.va.vro.bip.model.cancel.CancelClaimResponse;
import gov.va.vro.bip.model.claim.GetClaimResponse;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.GetClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsResponse;
import gov.va.vro.bip.model.lifecycle.PutClaimLifecycleRequest;
import gov.va.vro.bip.model.lifecycle.PutClaimLifecycleResponse;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionRequest;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionResponse;
import gov.va.vro.metricslogging.IMetricLoggerService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;

/**
 * BIP claim API service.
 *
 * @author warren @Date 10/31/22
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ComponentScan("gov.va.vro.metricslogging")
public class BipApiService implements IBipApiService {
  static final String CLAIM_DETAILS = "/claims/%s";
  static final String CANCEL_CLAIM = "/claims/%s/cancel";
  static final String TEMP_STATION_OF_JURISDICTION = "/claims/%s/temporary_station_of_jurisdiction";
  static final String CLAIM_LIFECYCLE_STATUS = "/claims/%s/lifecycle_status";
  static final String CONTENTION = "/claims/%s/contentions";
  static final String SPECIAL_ISSUE_TYPES = "/contentions/special_issue_types";

  @Qualifier("bipCERestTemplate")
  @NonNull
  final RestTemplate restTemplate;

  final BipApiProps bipApiProps;

  final ObjectMapper mapper;

  final IMetricLoggerService metricLogger;

  @Override
  public GetClaimResponse getClaimDetails(long claimId) {
    String url = bipApiProps.getClaimRequestUrl(String.format(CLAIM_DETAILS, claimId));

    return makeRequest(url, HttpMethod.GET, null, GetClaimResponse.class);
  }

  @Override
  public PutClaimLifecycleResponse putClaimLifecycleStatus(PutClaimLifecycleRequest request) {
    long claimId = request.getClaimId();
    String url = bipApiProps.getClaimRequestUrl(String.format(CLAIM_LIFECYCLE_STATUS, claimId));
    Map<String, Object> requestBody =
        Map.of("claimLifecycleStatus", request.getClaimLifecycleStatus());
    return makeRequest(url, HttpMethod.PUT, requestBody, PutClaimLifecycleResponse.class);
  }

  @Override
  public GetClaimContentionsResponse getClaimContentions(long claimId) {
    String url = bipApiProps.getClaimRequestUrl(String.format(CONTENTION, claimId));

    return makeRequest(url, HttpMethod.GET, null, GetClaimContentionsResponse.class);
  }

  @Override
  public CreateClaimContentionsResponse createClaimContentions(
      CreateClaimContentionsRequest request) {
    long claimId = request.getClaimId();
    String url = bipApiProps.getClaimRequestUrl(String.format(CONTENTION, claimId));
    Map<String, Object> requestBody = Map.of("createContentions", request.getCreateContentions());
    return makeRequest(url, HttpMethod.POST, requestBody, CreateClaimContentionsResponse.class);
  }

  @Override
  public UpdateClaimContentionsResponse updateClaimContentions(
      UpdateClaimContentionsRequest request) {
    long claimId = request.getClaimId();
    String url = bipApiProps.getClaimRequestUrl(String.format(CONTENTION, claimId));
    Map<String, Object> requestBody = Map.of("updateContentions", request.getUpdateContentions());
    return makeRequest(url, HttpMethod.PUT, requestBody, UpdateClaimContentionsResponse.class);
  }

  @Override
  public CancelClaimResponse cancelClaim(CancelClaimRequest request) {
    long claimId = request.getClaimId();
    String url = bipApiProps.getClaimRequestUrl(String.format(CANCEL_CLAIM, claimId));

    String lifecycleStatusReasonCode = request.getLifecycleStatusReasonCode();
    String closeReasonText = request.getCloseReasonText();
    Map<String, String> requestBody =
        Map.of(
            "lifecycleStatusReasonCode", lifecycleStatusReasonCode,
            "closeReasonText", closeReasonText);

    return makeRequest(url, HttpMethod.PUT, requestBody, CancelClaimResponse.class);
  }

  @Override
  public PutTempStationOfJurisdictionResponse putTempStationOfJurisdiction(
      PutTempStationOfJurisdictionRequest request) {
    long claimId = request.getClaimId();
    String url =
        bipApiProps.getClaimRequestUrl(String.format(TEMP_STATION_OF_JURISDICTION, claimId));

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("tempStationOfJurisdiction", request.getTempStationOfJurisdiction());

    return makeRequest(
        url, HttpMethod.PUT, requestBody, PutTempStationOfJurisdictionResponse.class);
  }

  @SuppressWarnings("unchecked")
  private <T extends BipPayloadResponse> T makeRequest(
      String url, HttpMethod method, Object requestBody, Class<T> expectedResponse) {

    try {

      HttpEntity<Object> httpEntity = new HttpEntity<>(requestBody, getBipHeader());
      log.info("event=requestSent url={} method={}", url, method);
      metricLogger.submitCount(
          IMetricLoggerService.METRIC.REQUEST_START,
          new String[] {
            String.format("expectedResponse:%s", expectedResponse.getSimpleName()),
            "source:bipApiService",
            String.format("method:%s", method.name())
          });

      long requestStartTime = System.nanoTime();
      ResponseEntity<T> bipResponse =
          restTemplate.exchange(url, method, httpEntity, expectedResponse);

      log.info(
          "event=responseReceived url={} method={} status={}",
          url,
          method,
          bipResponse.getStatusCode().value());
      metricLogger.submitRequestDuration(
          requestStartTime,
          System.nanoTime(),
          new String[] {
            String.format("expectedResponse:%s", expectedResponse.getSimpleName()),
            "source:bipApiService",
            String.format("method:%s", method.name())
          });

      BipPayloadResponse.BipPayloadResponseBuilder<?, ?> responseBuilder;
      if (bipResponse.hasBody()) {
        responseBuilder = Objects.requireNonNull(bipResponse.getBody()).toBuilder();
      } else {
        responseBuilder = mapper.readValue("{}", expectedResponse).toBuilder();
      }

      metricLogger.submitCount(
          IMetricLoggerService.METRIC.RESPONSE_COMPLETE,
          new String[] {
            String.format("expectedResponse:%s", expectedResponse.getSimpleName()),
            "source:bipApiService",
            String.format("method:%s", method.name())
          });

      return (T)
          responseBuilder
              .statusCode(bipResponse.getStatusCode().value())
              .statusMessage(HttpStatus.valueOf(bipResponse.getStatusCode().value()).name())
              .build();
    } catch (HttpStatusCodeException e) {
      log.info(
          "event=responseReceived url={} method={} status={} statusMessage={}",
          url,
          method,
          e.getStatusCode(),
          ((HttpStatus) e.getStatusCode()).name());
      throw e;
    } catch (Exception e) {
      log.error(
          "event=requestFailed url={} method={} status={} error={}",
          url,
          method,
          HttpStatus.INTERNAL_SERVER_ERROR.value(),
          e.getMessage());

      metricLogger.submitCount(
          IMetricLoggerService.METRIC.RESPONSE_ERROR,
          new String[] {
            String.format("expectedResponse:%s", expectedResponse.getSimpleName()),
            "source:bipApiService",
            String.format("method:%s", method.name()),
            String.format("error:%s", e.getMessage())
          });

      throw new BipException(e.getMessage(), e);
    }
  }

  /**
   * Verifies that the BIP Api responds to a request. Calls the special_issue_types URL and confirms
   * the response status is OK and body is not empty
   *
   * @return true if the API responds with OK status and response.
   */
  public boolean isApiFunctioning() {
    String url = bipApiProps.getClaimRequestUrl(SPECIAL_ISSUE_TYPES);
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
    HttpHeaders bipHttpHeaders = new HttpHeaders();
    bipHttpHeaders.setContentType(MediaType.APPLICATION_JSON);

    String jwt = createJwt();
    bipHttpHeaders.add("Authorization", "Bearer " + jwt);
    return bipHttpHeaders;
  }

  public String createJwt() {
    // Assuming these methods and variables are correctly defined in your class
    Claims claims = bipApiProps.toCommonJwtClaims();
    String issuer = bipApiProps.getClaimIssuer();
    String secret = bipApiProps.getClaimSecret();

    // Define the signing key
    byte[] signSecretBytes = secret.getBytes(StandardCharsets.UTF_8);
    Key signingKey = new SecretKeySpec(signSecretBytes, "HmacSHA256");

    // Set the expiration as an example (e.g., 1 hour from now)
    long currentTimeMillis = System.currentTimeMillis();
    Date expiryDate = new Date(currentTimeMillis + 3600000);

    // Build the JWT
    return Jwts.builder()
        .claims(claims)
        .issuer(issuer)
        .subject("Claim")
        .issuedAt(new Date(currentTimeMillis))
        .expiration(expiryDate)
        .signWith(signingKey)
        .header()
        .add("alg", "HS256")
        .and()
        .compact();
  }
}
