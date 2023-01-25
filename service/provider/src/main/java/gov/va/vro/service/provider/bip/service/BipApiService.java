package gov.va.vro.service.provider.bip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.bip.BipClaim;
import gov.va.vro.model.bip.BipClaimResp;
import gov.va.vro.model.bip.BipContentionResp;
import gov.va.vro.model.bip.BipFileUploadPayload;
import gov.va.vro.model.bip.BipFileUploadResp;
import gov.va.vro.model.bip.BipUpdateClaimResp;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.ClaimStatus;
import gov.va.vro.model.bip.CreateContentionReq;
import gov.va.vro.model.bip.FileIdType;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import javax.crypto.spec.SecretKeySpec;

/**
 * BIP claim API service.
 *
 * @author warren @Date 10/31/22
 */
@Service
@Conditional(BipConditions.HigherEnvCondition.class)
@RequiredArgsConstructor
@Slf4j
public class BipApiService implements IBipApiService {
  private static final String CLAIM_DETAILS = "/claims/%s";
  private static final String UPDATE_CLAIM_STATUS = "/claims/%s/lifecycle_status";
  private static final String CONTENTION = "/claims/%s/contentions";
  private static final String X_FOLDER_URI = "VETERAN:%s:%s";
  private static final String HTTPS = "https://";

  private static final String UPLOAD_FILE = "/files";

  @Qualifier("bipRestTemplate")
  @NonNull
  private final RestTemplate restTemplate;

  @Qualifier("bipCERestTemplate")
  @NonNull
  private final RestTemplate ceRestTemplate;

  private final BipApiProps bipApiProps;

  private final ObjectMapper mapper = new ObjectMapper();

  private enum API {
    CLAIM,
    EVIDENCE
  }

  @Override
  public BipClaim getClaimDetails(long claimId) throws BipException {
    try {
      String url = HTTPS + bipApiProps.getClaimBaseUrl() + String.format(CLAIM_DETAILS, claimId);
      log.info("call {} to get claim info.", url);
      HttpHeaders headers = getBipHeader(API.CLAIM);
      log.info("jwt: {}", headers.get("Authorization")); // TODO: remove it after test.
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
        throw new BipException(bipResponse.getStatusCode() + " " + bipResponse.getBody());
      }
    } catch (RestClientException | JsonProcessingException e) {
      log.error("failed to get claim info for claim ID {}.", claimId, e);
      throw new BipException(e.getMessage(), e);
    }
  }

  /**
   * Updates claim status.
   *
   * @param claimId claim ID for the claim to be updated // * @param statusCodeMsg the new status.
   * @return a list of messages.
   * @throws BipException error occurs
   */
  @Override
  public BipUpdateClaimResp setClaimToRfdStatus(long claimId) throws BipException {
    try {
      String url =
          HTTPS + bipApiProps.getClaimBaseUrl() + String.format(UPDATE_CLAIM_STATUS, claimId);
      log.info("call {} to set claim RFD status.", url);
      HttpHeaders headers = getBipHeader(API.CLAIM);
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("claimLifecycleStatus", ClaimStatus.RFD.getDescription());
      HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(requestBody, headers);
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
      return new BipUpdateClaimResp(bipResponse.getStatusCode(), bipResponse.getBody());
    } catch (RestClientException e) {
      log.error("failed to update status to {} for claim {}.", ClaimStatus.RFD, claimId, e);
      throw new BipException(e.getMessage(), e);
    }
  }

  @Override
  public BipUpdateClaimResp updateClaimStatus(long claimId, ClaimStatus status)
      throws BipException {
    try {
      String url =
          HTTPS + bipApiProps.getClaimBaseUrl() + String.format(UPDATE_CLAIM_STATUS, claimId);
      log.info("call {} to update claim status to {}.", url, status);

      HttpHeaders headers = getBipHeader(API.CLAIM);
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("claimLifecycleStatus", status.getDescription());
      HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(requestBody, headers);
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
      return new BipUpdateClaimResp(bipResponse.getStatusCode(), bipResponse.getBody());
    } catch (RestClientException e) {
      log.error("failed to update status to {} for claim {}.", ClaimStatus.RFD, claimId, e);
      throw new BipException(e.getMessage(), e);
    }
  }

  @Override
  public List<ClaimContention> getClaimContentions(long claimId) throws BipException {
    try {
      String url = HTTPS + bipApiProps.getClaimBaseUrl() + String.format(CONTENTION, claimId);
      log.info("Call {} to get claim contention for {}.", url, claimId);
      HttpHeaders headers = getBipHeader(API.CLAIM);
      HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(headers);
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
      if (HttpStatus.OK.equals(bipResponse.getStatusCode())) {
        BipContentionResp resp = mapper.readValue(bipResponse.getBody(), BipContentionResp.class);
        return resp.getContentions();
      } else if (HttpStatus.NO_CONTENT.equals(bipResponse.getStatusCode())) {
        return new ArrayList<>();
      } else {
        log.error(
            "getClaimContentions returned {} for {}. {}",
            bipResponse.getStatusCode(),
            claimId,
            bipResponse.getBody());
        throw new BipException(bipResponse.getBody());
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
      HttpHeaders headers = getBipHeader(API.CLAIM);
      String updtContention = mapper.writeValueAsString(contention);
      HttpEntity<String> httpEntity = new HttpEntity<>(updtContention, headers);
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
      return new BipUpdateClaimResp(bipResponse.getStatusCode(), bipResponse.getBody());
    } catch (RestClientException | JsonProcessingException e) {
      log.error("failed to getClaimContentions for claim {}.", claimId, e);
      throw new BipException(e.getMessage(), e);
    }
  }

  @Override
  public BipUpdateClaimResp addClaimContention(long claimId, CreateContentionReq contention)
      throws BipException {
    try {
      String url = HTTPS + bipApiProps.getClaimBaseUrl() + String.format(CONTENTION, claimId);
      log.info("Call {} to add claim contention for {}.", url, claimId);
      HttpHeaders headers = getBipHeader(API.CLAIM);
      String createContention = mapper.writeValueAsString(contention);
      HttpEntity<String> request = new HttpEntity<>(createContention, headers);
      log.info("createContesion: \n {}", createContention);
      ResponseEntity<String> bipResponse = restTemplate.postForEntity(url, request, String.class);
      return new BipUpdateClaimResp(bipResponse.getStatusCode(), bipResponse.getBody());
    } catch (RestClientException | JsonProcessingException e) {
      log.error("failed to addClaimContentions for claim {}.", claimId, e);
      throw new BipException(e.getMessage(), e);
    }
  }

  @Override
  public BipFileUploadResp uploadEvidence(
      FileIdType idtype, String fileId, BipFileUploadPayload fileUploadPayload, File file)
      throws BipException { // TODO: to be finished with certificate set.
    try {
      String url = HTTPS + bipApiProps.getEvidenceBaseUrl() + UPLOAD_FILE;
      log.info("call {} to uploadEvidence file for {} : {}", url, idtype.name(), fileId);
      HttpHeaders headers = getBipHeader(API.EVIDENCE);
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      headers.set("X-Folder-URI", String.format(X_FOLDER_URI, idtype.name(), fileId));

      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("payLoad", mapper.writeValueAsString(fileUploadPayload));
      body.add("file", new FileSystemResource(file));
      HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);

      ResponseEntity<String> bipResponse =
          ceRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
      BipFileUploadResp resp = new BipFileUploadResp();
      resp.setStatus(bipResponse.getStatusCode());
      resp.setMessage(bipResponse.getBody());
      return resp;
    } catch (RestClientException | JsonProcessingException e) {
      log.error("failed to upload file.", e);
      throw new BipException(e.getMessage(), e);
    }
  }

  @Override
  public BipFileUploadResp uploadEvidenceFile(
      FileIdType idtype, String fileId, BipFileUploadPayload uploadEvidenceReq, MultipartFile file)
      throws BipException {
    try {
      String url = HTTPS + bipApiProps.getEvidenceBaseUrl() + UPLOAD_FILE;
      log.info("Call {} to uploadEvidenceFile for {} : {}", url, idtype.name(), fileId);

      HttpHeaders headers = getBipHeader(API.EVIDENCE);
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);
      headers.set("X-Folder-URI", String.format(X_FOLDER_URI, idtype.name(), fileId));

      String filename = file.getOriginalFilename();
      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("payload", mapper.writeValueAsString(uploadEvidenceReq));

      ByteArrayResource contentsAsResource =
          new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
              return filename; // Filename has to be returned in order to be able to post.
            }
          };

      body.add("file", contentsAsResource);
      HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);

      ResponseEntity<String> bipResponse =
          ceRestTemplate.postForEntity(url, httpEntity, String.class);

      BipFileUploadResp resp = new BipFileUploadResp();
      log.info(
          "bip response for upload: status: {}, message: {}",
          bipResponse.getStatusCode(),
          bipResponse.getBody());
      resp.setStatus(bipResponse.getStatusCode());
      resp.setMessage(mapper.writeValueAsString(bipResponse.getBody()));
      return resp;
    } catch (RestClientException | IOException e) {
      log.error("failed to upload file.", e);
      throw new BipException(e.getMessage(), e);
    }
  }

  private HttpHeaders getBipHeader(API api) throws BipException {
    try {
      HttpHeaders bipHttpHeaders = new HttpHeaders();
      bipHttpHeaders.setContentType(MediaType.APPLICATION_JSON);

      String jwt = createJwt(api);
      bipHttpHeaders.add("Authorization", "Bearer " + jwt);
      return bipHttpHeaders;
    } catch (Exception e) {
      log.error("Failed to build BIP HTTP Headers.", e);
      throw new BipException(e.getMessage(), e);
    }
  }

  private String createJwt(API api) throws BipException {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, 30);
    Date expired = cal.getTime();
    Claims claims = Jwts.claims();
    claims.put("jti", bipApiProps.getJti());
    claims.put("applicationID", bipApiProps.getApplicationId());
    claims.put("stationID", bipApiProps.getStationId());
    claims.put("userID", bipApiProps.getClaimClientId());
    Date now = cal.getTime();
    claims.put("iat", now.getTime());
    claims.put("expires", expired.getTime());
    Map<String, Object> headerType = new HashMap<>();
    headerType.put("typ", Header.JWT_TYPE);

    switch (api) {
      case CLAIM -> {
        claims.put("iss", bipApiProps.getClaimIssuer());
        byte[] signSecretBytes = bipApiProps.getClaimSecret().getBytes(StandardCharsets.UTF_8);
        Key signingKey = new SecretKeySpec(signSecretBytes, SignatureAlgorithm.HS256.getJcaName());
        return Jwts.builder()
            .setSubject("Claim")
            .setIssuedAt(now)
            .setExpiration(expired)
            .setClaims(claims)
            .signWith(SignatureAlgorithm.HS256, signingKey)
            .setHeaderParams(headerType)
            .compact();
      }
      case EVIDENCE -> {
        byte[] signSecretBytes = bipApiProps.getEvidenceSecret().getBytes(StandardCharsets.UTF_8);
        SecretKeySpec signingKey =
            new SecretKeySpec(signSecretBytes, SignatureAlgorithm.HS256.getJcaName());
        claims.put("iss", bipApiProps.getEvidenceIssuer());
        return Jwts.builder()
            .setSubject("Evidence")
            .setIssuedAt(now)
            .setExpiration(expired)
            .setClaims(claims)
            .signWith(SignatureAlgorithm.HS256, signingKey)
            .setHeaderParams(headerType)
            .compact();
      }
      default -> throw new BipException("Invalid API specification. " + api.name());
    }
  }
}
