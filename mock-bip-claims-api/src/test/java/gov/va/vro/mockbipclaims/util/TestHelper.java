package gov.va.vro.mockbipclaims.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.mockbipclaims.model.bip.ClaimDetail;
import gov.va.vro.mockbipclaims.model.bip.ContentionSummary;
import gov.va.vro.mockbipclaims.model.bip.ExistingContention;
import gov.va.vro.mockbipclaims.model.bip.request.UpdateClaimLifecycleStatusRequest;
import gov.va.vro.mockbipclaims.model.bip.request.UpdateContentionsRequest;
import gov.va.vro.mockbipclaims.model.bip.response.ClaimDetailResponse;
import gov.va.vro.mockbipclaims.model.bip.response.ContentionSummariesResponse;
import gov.va.vro.mockbipclaims.model.bip.response.UpdateClaimLifecycleStatusResponse;
import gov.va.vro.mockbipclaims.model.bip.response.UpdateContentionsResponse;
import gov.va.vro.mockbipclaims.model.mock.response.UpdatesResponse;
import gov.va.vro.mockshared.jwt.JwtGenerator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class TestHelper {
  @Autowired
  @Qualifier("httpsRestTemplate")
  private RestTemplate restTemplate;

  @Autowired private JwtGenerator jwtGenerator;

  private HttpHeaders getHeaders(TestSpec spec) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (!spec.isIgnoreJwt()) {
      String jwt = jwtGenerator.generate();
      log.info("jwt: {}", jwt);
      headers.set("Authorization", "Bearer " + jwt);
    }
    return headers;
  }

  /**
   * Gets the response entity for the claim specified by the spec.
   *
   * @param spec Test Specification
   * @return Response Entity
   */
  @SneakyThrows
  public ResponseEntity<ClaimDetailResponse> getClaim(TestSpec spec) {
    final long claimId = spec.getClaimId();

    HttpHeaders headers = getHeaders(spec);
    HttpEntity<Object> request = new HttpEntity<Object>(headers);

    String url = spec.getUrl("/claims/" + claimId);
    return restTemplate.exchange(url, HttpMethod.GET, request, ClaimDetailResponse.class);
  }

  /**
   * Gets the claim specified by the spec.
   *
   * @param spec test specification
   * @return ClaimDetail object
   */
  @SneakyThrows
  public ClaimDetail getClaimDetail(TestSpec spec) {
    ResponseEntity<ClaimDetailResponse> response = getClaim(spec);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ClaimDetailResponse body = response.getBody();
    return body.getClaim();
  }

  /**
   * Gets the response entity for the contentions specified by the spec.
   *
   * @param spec test specification
   * @return ResponseEntity
   */
  @SneakyThrows
  public ResponseEntity<ContentionSummariesResponse> getContentions(TestSpec spec) {
    final long claimId = spec.getClaimId();

    HttpHeaders headers = getHeaders(spec);
    HttpEntity<Object> request = new HttpEntity<Object>(headers);

    String url = spec.getUrl("/claims/" + claimId + "/contentions");
    return restTemplate.exchange(url, HttpMethod.GET, request, ContentionSummariesResponse.class);
  }

  /**
   * Updates the claim contentions specified by the spec.
   *
   * @param spec test specification
   * @param contention updated contention
   * @return response entity after put
   */
  @SneakyThrows
  public ResponseEntity<UpdateContentionsResponse> putContentions(
      TestSpec spec, ExistingContention contention) {
    final long claimId = spec.getClaimId();

    HttpHeaders headers = getHeaders(spec);
    var body = new UpdateContentionsRequest();
    body.addUpdateContentionsItem(contention);

    HttpEntity<UpdateContentionsRequest> request = new HttpEntity<>(body, headers);

    String url = spec.getUrl("/claims/" + claimId + "/contentions");
    return restTemplate.exchange(url, HttpMethod.PUT, request, UpdateContentionsResponse.class);
  }

  /**
   * Gets the contentions specified by the spec.
   *
   * @param spec test specification
   * @return List of contention summary objects
   */
  public List<ContentionSummary> getContentionSummaries(TestSpec spec) {
    ResponseEntity<ContentionSummariesResponse> response = getContentions(spec);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ContentionSummariesResponse csr = response.getBody();
    return csr.getContentions();
  }

  /**
   * Update the claim lifecycle specified by the spec.
   *
   * @param spec test specification
   * @param value new lifecycle
   * @return response entity after put
   */
  @SneakyThrows
  public ResponseEntity<UpdateClaimLifecycleStatusResponse> putLifecycleStatus(
      TestSpec spec, String value) {
    final long claimId = spec.getClaimId();

    HttpHeaders headers = getHeaders(spec);
    var body = new UpdateClaimLifecycleStatusRequest();
    body.setClaimLifecycleStatus(value);

    HttpEntity<UpdateClaimLifecycleStatusRequest> request = new HttpEntity<>(body, headers);

    String url = spec.getUrl("/claims/" + claimId + "/lifecycle_status");
    return restTemplate.exchange(
        url, HttpMethod.PUT, request, UpdateClaimLifecycleStatusResponse.class);
  }

  /**
   * Retrieves if lifecycle status of a claim is updated.
   *
   * @param spec test specification
   * @return is updated?
   */
  public boolean isLifecycleStatusUpdated(TestSpec spec) {
    String url = spec.getUrl("/updates/" + spec.getClaimId() + "/lifecycle_status");
    UpdatesResponse response = restTemplate.getForObject(url, UpdatesResponse.class);
    return response.isFound();
  }

  /**
   * Retrieves if the contentions of a claim is updated.
   *
   * @param spec test specification
   * @return is updated?
   */
  public boolean isContentionsUpdated(TestSpec spec) {
    String url = spec.getUrl("/updates/" + spec.getClaimId() + "/contentions");
    UpdatesResponse response = restTemplate.getForObject(url, UpdatesResponse.class);
    return response.isFound();
  }

  /**
   * Retrieves if the contentions of a claim is updated.
   *
   * @param spec test specification
   */
  public void resetUpdated(TestSpec spec) {
    String url = spec.getUrl("/updates/" + spec.getClaimId());
    restTemplate.delete(url);
  }
}
