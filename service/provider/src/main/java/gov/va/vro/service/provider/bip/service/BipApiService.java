package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.BipUpdateClaimStatusResp;
import gov.va.vro.service.provider.bip.BipApiProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * BIP claim API service.
 *
 * <p>* @author warren @Date 10/31/22
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BipApiService {
  private static final String UPDATE_CLAIM_STATUS = "/claim/%s/lifecycle_status";

  private final RestTemplate restTemplate;
  private final BipApiProps bipApiProps;

  /**
   * Updates claim status.
   *
   * @param claimId claim ID for the claim to be updated
   * @param statusCodeMsg the new status.
   * @return a list of messages.
   * @throws BipException error occurs
   */
  public BipUpdateClaimStatusResp updateClaimStatus(String claimId, String statusCodeMsg)
      throws BipException {
    try {
      String url = bipApiProps.getBaseURL() + String.format(UPDATE_CLAIM_STATUS, claimId);
      HttpHeaders headers = getBipHeader();
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("claimLifecyclesStatus", statusCodeMsg);
      HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(requestBody, headers);
      ResponseEntity<String> bipResponse =
          restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
      BipUpdateClaimStatusResp resp =
          new BipUpdateClaimStatusResp(
              bipResponse.getStatusCode().equals(HttpStatus.OK), bipResponse.getBody());
      return resp;
    } catch (RestClientException e) {
      log.error("failed to update status to {} for claim {}.", statusCodeMsg, claimId, e);
      throw new BipException(e.getMessage(), e);
    }
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
