package gov.va.vro.bip.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.model.BipMessage;
import gov.va.vro.bip.model.BipPayloadResponse;
import gov.va.vro.bip.model.cancel.CancelClaimRequest;
import gov.va.vro.bip.model.cancel.CancelClaimResponse;
import gov.va.vro.bip.model.claim.GetClaimRequest;
import gov.va.vro.bip.model.claim.GetClaimResponse;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.GetClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.GetClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.GetSpecialIssueTypesRequest;
import gov.va.vro.bip.model.contentions.GetSpecialIssueTypesResponse;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;

/**
 * BipApiService offers an implementation of IBipApiService as an extension of the BIP Claims REST
 * API Service
 */
@Service
@Slf4j
@ComponentScan("gov.va.vro.metricslogging")
@RequiredArgsConstructor
public class BipApiService implements IBipApiService {

  @Qualifier("bipCERestTemplate")
  @NonNull
  final RestTemplate restTemplate;

  final BipApiProps bipApiProps;

  final ObjectMapper mapper;

  final IMetricLoggerService metricLogger;
  public static final String METRICS_PREFIX = "vro_bip";

  static final String CLAIM_DETAILS = "/claims/%s";
  static final String CANCEL_CLAIM = "/claims/%s/cancel";
  static final String TEMP_STATION_OF_JURISDICTION = "/claims/%s/temporary_station_of_jurisdiction";
  static final String CLAIM_LIFECYCLE_STATUS = "/claims/%s/lifecycle_status";
  static final String CONTENTION = "/claims/%s/contentions";
  static final String SPECIAL_ISSUE_TYPES = "/contentions/special_issue_types";

  @Override
  public GetClaimResponse getClaimDetails(GetClaimRequest request) {
    String url = bipApiProps.getClaimRequestUrl(String.format(CLAIM_DETAILS, request.getClaimId()));

    return makeRequest(
        url,
        HttpMethod.GET,
        null,
        getBipHeader(request.getExternalUserId(), request.getExternalKey()),
        GetClaimResponse.class);
  }

  @Override
  public PutClaimLifecycleResponse putClaimLifecycleStatus(PutClaimLifecycleRequest request) {
    long claimId = request.getClaimId();
    String url = bipApiProps.getClaimRequestUrl(String.format(CLAIM_LIFECYCLE_STATUS, claimId));
    Map<String, Object> requestBody =
        Map.of("claimLifecycleStatus", request.getClaimLifecycleStatus());
    return makeRequest(
        url,
        HttpMethod.PUT,
        requestBody,
        getBipHeader(request.getExternalUserId(), request.getExternalKey()),
        PutClaimLifecycleResponse.class);
  }

  @Override
  public GetClaimContentionsResponse getClaimContentions(GetClaimContentionsRequest request) {
    String url = bipApiProps.getClaimRequestUrl(String.format(CONTENTION, request.getClaimId()));

    return makeRequest(
        url,
        HttpMethod.GET,
        null,
        getBipHeader(request.getExternalUserId(), request.getExternalKey()),
        GetClaimContentionsResponse.class);
  }

  @Override
  public CreateClaimContentionsResponse createClaimContentions(
      CreateClaimContentionsRequest request) {
    long claimId = request.getClaimId();
    String url = bipApiProps.getClaimRequestUrl(String.format(CONTENTION, claimId));
    Map<String, Object> requestBody = Map.of("createContentions", request.getCreateContentions());
    return makeRequest(
        url,
        HttpMethod.POST,
        requestBody,
        getBipHeader(request.getExternalUserId(), request.getExternalKey()),
        CreateClaimContentionsResponse.class);
  }

  @Override
  public UpdateClaimContentionsResponse updateClaimContentions(
      UpdateClaimContentionsRequest request) {
    long claimId = request.getClaimId();
    String url = bipApiProps.getClaimRequestUrl(String.format(CONTENTION, claimId));
    Map<String, Object> requestBody = Map.of("updateContentions", request.getUpdateContentions());
    return makeRequest(
        url,
        HttpMethod.PUT,
        requestBody,
        getBipHeader(request.getExternalUserId(), request.getExternalKey()),
        UpdateClaimContentionsResponse.class);
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

    return makeRequest(
        url,
        HttpMethod.PUT,
        requestBody,
        getBipHeader(request.getExternalUserId(), request.getExternalKey()),
        CancelClaimResponse.class);
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
        url,
        HttpMethod.PUT,
        requestBody,
        getBipHeader(request.getExternalUserId(), request.getExternalKey()),
        PutTempStationOfJurisdictionResponse.class);
  }

  @Override
  public GetSpecialIssueTypesResponse getSpecialIssueTypes(GetSpecialIssueTypesRequest request) {
    String url = bipApiProps.getClaimRequestUrl(SPECIAL_ISSUE_TYPES);
    return makeRequest(
        url,
        HttpMethod.GET,
        null,
        getBipHeader(request.getExternalUserId(), request.getExternalKey()),
        GetSpecialIssueTypesResponse.class);
  }

  @SuppressWarnings("unchecked")
  private <T extends BipPayloadResponse> T makeRequest(
      String url,
      HttpMethod method,
      Object requestBody,
      HttpHeaders headers,
      Class<T> expectedResponse) {

    try {

      HttpEntity<Object> httpEntity = new HttpEntity<>(requestBody, headers);
      log.info(
          "event=requestSent url={} method={} auth={}", url, method, headers.get("Authorization"));
      metricLogger.submitCount(
          METRICS_PREFIX,
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
          METRICS_PREFIX,
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
          METRICS_PREFIX,
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

      String errors;
      try {
        T response = mapper.readValue(e.getResponseBodyAsString(), expectedResponse);
        List<BipMessage> messages =
            new ArrayList<>(
                response.getMessages() != null ? response.getMessages() : new ArrayList<>());
        errors = messages.toString();
      } catch (Exception ex) {
        errors = ex.getMessage();
      }

      log.info(
          "event=responseReceived url={} method={} status={} statusMessage={} errors={}",
          url,
          method,
          e.getStatusCode(),
          ((HttpStatus) e.getStatusCode()).name(),
          errors);
      throw e;
    } catch (Exception e) {
      log.error(
          "event=requestFailed url={} method={} status={} error={}",
          url,
          method,
          HttpStatus.INTERNAL_SERVER_ERROR.value(),
          e.getMessage());

      metricLogger.submitCount(
          METRICS_PREFIX,
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
    String url = bipApiProps.getAvailabilityUrl();
    log.info("Call {} to confirm service availability", url);

    HttpHeaders headers = getBipHeader(null, null);
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

  HttpHeaders getBipHeader(String externalUserId, String externalKey) throws BipException {
    HttpHeaders bipHttpHeaders = new HttpHeaders();
    bipHttpHeaders.setContentType(MediaType.APPLICATION_JSON);

    Claims claims = bipApiProps.toCommonJwtClaims(externalUserId, externalKey);
    String secret = bipApiProps.getClaimSecret();

    // Define the signing key
    byte[] signSecretBytes = secret.getBytes(StandardCharsets.UTF_8);
    Key signingKey = new SecretKeySpec(signSecretBytes, "HmacSHA256");

    // Build the JWT
    String jwt =
        Jwts.builder()
            .claims(claims)
            .signWith(signingKey)
            .header()
            .add("alg", "HS256")
            .and()
            .compact();
    bipHttpHeaders.add("Authorization", "Bearer " + jwt);
    return bipHttpHeaders;
  }
}
