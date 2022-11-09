package gov.va.vro.service.provider.bip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.service.provider.bip.BipApiProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BIP claim API service.
 *
 * <p>* @author warren @Date 10/31/22
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BipApiService implements IBipApiService {
  private static final String GET_CLAIM_DETAILS = "/claim/%s";
  private static final String UPDATE_CLAIM_STATUS = "/claim/%s/lifecycle_status";
  private static final String GET_CONTENTION = "/claim/%s/contention";
  private static final String UPDATE_CONTENTION = "/claim/contention";
  private static final String RFD_STATUS = "Ready For Decision";

  private final RestTemplate restTemplate;
  private final BipApiProps bipApiProps;

  @Override
  public HashMap<String, String> getClaimDetails(Integer collectionId) throws BipException {
    return null;
  }

  /**
   * Updates claim status.
   *
   * @param claimId claim ID for the claim to be updated // * @param statusCodeMsg the new status.
   * @return a list of messages.
   * @throws BipException error occurs
   */
  //  public BipUpdateClaimStatusResp updateClaimStatus(String claimId, String statusCodeMsg)
  //      throws BipException {
  public HashMap<String, String> updateClaimStatus(Integer claimId) throws BipException {
    try {
      String url = bipApiProps.getBaseURL() + String.format(UPDATE_CLAIM_STATUS, claimId);
      HttpHeaders headers = getBipHeader();
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("claimLifecyclesStatus", RFD_STATUS);
      HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(requestBody, headers);
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
      //      BipUpdateClaimStatusResp resp =
      //          new BipUpdateClaimStatusResp(
      //              bipResponse.getStatusCode().equals(HttpStatus.OK), bipResponse.getBody());
      //      return resp;
      HashMap<String, String> resp = new HashMap<>();
      resp.put("status", bipResponse.getStatusCode().name());
      resp.put("message", bipResponse.getBody());
      return resp;
    } catch (RestClientException e) {
      log.error("failed to update status to {} for claim {}.", RFD_STATUS, claimId, e);
      throw new BipException(e.getMessage(), e);
    }
  }

  @Override
  public List<ClaimContention> getClaimContentions(Integer claimId) throws BipException {
    try {
      String url = bipApiProps.getBaseURL() + String.format(GET_CONTENTION, claimId);
      HttpHeaders headers = getBipHeader();
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("requestbody", RFD_STATUS);
      HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(requestBody, headers);
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
      if (HttpStatus.OK.equals(bipResponse.getStatusCode())) {
        ObjectMapper mapper = new ObjectMapper();
        List<ClaimContention> resp =
            mapper.readValue(
                bipResponse.getBody(),
                mapper.getTypeFactory().constructCollectionType(List.class, ClaimContention.class));
        return resp;
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
  public HashMap<String, String> updateClaimContention(Integer claimId, ClaimContention contention)
      throws BipException {
    return null;
  }

  @Override
  public HashMap<String, String> uploadEvidence(HashMap<String, Object> uploadEvidenceReq)
      throws BipException {
    return null;
  }

  private HttpHeaders getBipHeader() throws BipException {
    try {
      HttpHeaders bipHttpHeaders = new HttpHeaders();
      bipHttpHeaders.setContentType(MediaType.APPLICATION_JSON);
      // TODO: set authorization header when we get the credentials.

      return bipHttpHeaders;
    } catch (Exception e) {
      log.error("Failed to build BIP HTTP Headers.", e);
      throw new BipException(e.getMessage(), e);
    }
  }
}
