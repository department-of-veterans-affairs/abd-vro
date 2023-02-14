package gov.va.vro.service.provider.bip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.bip.BipClaim;
import gov.va.vro.model.bip.BipClaimResp;
import gov.va.vro.model.bip.BipContentionResp;
import gov.va.vro.model.bip.BipUpdateClaimResp;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.ClaimStatus;
import gov.va.vro.model.bip.UpdateContentionReq;
import gov.va.vro.service.provider.BipApiProps;
import gov.va.vro.service.provider.bip.BipException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;

/**
 * BIP claim API service.
 *
 * @author warren @Date 10/31/22
 */
@Service
@Conditional(BipConditions.NonLocalEnvironmentCondition.class)
@RequiredArgsConstructor
@Slf4j
public class BipApiService implements IBipApiService {
  private static final String CLAIM_DETAILS = "/claims/%s";
  private static final String UPDATE_CLAIM_STATUS = "/claims/%s/lifecycle_status";
  private static final String CONTENTION = "/claims/%s/contentions";

  private static final String HTTPS = "https://";

  @Qualifier("bipCERestTemplate")
  @NonNull
  private final RestTemplate restTemplate;

  private final BipApiProps bipApiProps;

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public BipClaim getClaimDetails(long claimId) throws BipException {
    try {
      String url = HTTPS + bipApiProps.getClaimBaseUrl() + String.format(CLAIM_DETAILS, claimId);
      log.info("call {} to get claim info.", url);
      HttpHeaders headers = getBipHeader();
      HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(headers);
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
      if (bipResponse.getStatusCode() == HttpStatus.OK) {
        BipClaimResp result = mapper.readValue(bipResponse.getBody(), BipClaimResp.class);
        return result.getClaim();
      } else {
        log.error(
            "Failed to get claim details for {}. {} \n{}",
            claimId,
            bipResponse.getStatusCode(),
            bipResponse.getBody());
        throw new BipException(bipResponse.getStatusCode(), bipResponse.getBody());
      }
    } catch (RestClientException | JsonProcessingException e) {
      log.error("failed to get claim info for claim ID {}.", claimId, e);
      throw new BipException(e.getMessage(), e);
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
  public BipUpdateClaimResp setClaimToRfdStatus(long claimId) throws BipException {
    return updateClaimStatus(claimId, ClaimStatus.RFD);
  }

  @Override
  public BipUpdateClaimResp updateClaimStatus(long claimId, ClaimStatus status)
      throws BipException {
    try {
      String url =
          HTTPS + bipApiProps.getClaimBaseUrl() + String.format(UPDATE_CLAIM_STATUS, claimId);
      log.info("call {} to update claim status to {}.", url, status);

      HttpHeaders headers = getBipHeader();
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("claimLifecycleStatus", status.getDescription());
      HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(requestBody, headers);
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
      if (bipResponse.getStatusCode() == HttpStatus.OK) {
        return new BipUpdateClaimResp(HttpStatus.OK, bipResponse.getBody());
      } else {
        throw new BipException(bipResponse.getStatusCode(), bipResponse.getBody());
      }
    } catch (RestClientException e) {
      log.error("failed to update status to {} for claim {}.", status.name(), claimId, e);
      throw new BipException(e.getMessage(), e);
    }
  }

  @Override
  public List<ClaimContention> getClaimContentions(long claimId) throws BipException {
    try {
      String url = HTTPS + bipApiProps.getClaimBaseUrl() + String.format(CONTENTION, claimId);
      log.info("Call {} to get claim contention for {}.", url, claimId);
      HttpHeaders headers = getBipHeader();
      HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(headers);
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
      if (HttpStatus.OK == bipResponse.getStatusCode()) {
        BipContentionResp resp = mapper.readValue(bipResponse.getBody(), BipContentionResp.class);
        return resp.getContentions();
      } else if (HttpStatus.NO_CONTENT == bipResponse.getStatusCode()) {
        return new ArrayList<>();
      } else {
        log.error(
            "getClaimContentions returned {} for {}. {}",
            bipResponse.getStatusCode(),
            claimId,
            bipResponse.getBody());
        throw new BipException(bipResponse.getStatusCode(), bipResponse.getBody());
      }
    } catch (RestClientException | JsonProcessingException e) {
      log.error("failed to getClaimContentions for claim {}.", claimId, e);
      throw new BipException(e.getMessage(), e);
    }
  }

  @Override
  public BipUpdateClaimResp updateClaimContention(long claimId, UpdateContentionReq contention)
      throws BipException {
    try {
      String url = HTTPS + bipApiProps.getClaimBaseUrl() + String.format(CONTENTION, claimId);
      log.info("Call {} to update contention for {}.", url, claimId);
      HttpHeaders headers = getBipHeader();
      String updtContention = mapper.writeValueAsString(contention);
      HttpEntity<String> httpEntity = new HttpEntity<>(updtContention, headers);
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
      if (bipResponse.getStatusCode() == HttpStatus.OK) {
        return new BipUpdateClaimResp(HttpStatus.OK, bipResponse.getBody());
      } else {
        throw new BipException(bipResponse.getStatusCode(), bipResponse.getBody());
      }
    } catch (RestClientException | JsonProcessingException e) {
      log.error("failed to getClaimContentions for claim {}.", claimId, e);
      throw new BipException(e.getMessage(), e);
    }
  }

  private HttpHeaders getBipHeader() throws BipException {
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

  private String createJwt() throws BipException {
    Claims claims = bipApiProps.toCommonJwtClaims();
    Map<String, Object> headerType = new HashMap<>();
    headerType.put("typ", Header.JWT_TYPE);

    claims.put("iss", bipApiProps.getClaimIssuer());
    byte[] signSecretBytes = bipApiProps.getClaimSecret().getBytes(StandardCharsets.UTF_8);
    Key signingKey = new SecretKeySpec(signSecretBytes, SignatureAlgorithm.HS256.getJcaName());
    return Jwts.builder()
        .setSubject("Claim")
        .setIssuedAt(Calendar.getInstance().getTime())
        .setExpiration(claims.getExpiration())
        .setClaims(claims)
        .signWith(SignatureAlgorithm.HS256, signingKey)
        .setHeaderParams(headerType)
        .compact();
  }
}
