package gov.va.vro.mockbipclaims.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.mockbipclaims.model.ClaimDetail;
import gov.va.vro.mockbipclaims.model.ClaimDetailResponse;
import gov.va.vro.mockbipclaims.model.ClaimLifecycleStatusesResponse;
import gov.va.vro.mockbipclaims.model.ContentionSummariesResponse;
import gov.va.vro.mockbipclaims.model.ContentionSummary;
import gov.va.vro.mockbipclaims.model.ExistingContention;
import gov.va.vro.mockbipclaims.model.UpdateClaimLifecycleStatusRequest;
import gov.va.vro.mockbipclaims.model.UpdateContentionsRequest;
import gov.va.vro.mockbipclaims.model.UpdateContentionsResponse;
import lombok.SneakyThrows;
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
public class TestHelper {
  @Autowired
  @Qualifier("httpsRestTemplate")
  private RestTemplate restTemplate;

  @Autowired private JwtGenerator jwtGenerator;

  /**
   * Posts the file specified by spec.
   *
   * @param spec Test Specification
   * @return Response Entity
   */
  @SneakyThrows
  public ResponseEntity<ClaimDetailResponse> getClaim(TestSpec spec) {
    final long claimId = spec.getClaimId();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (!spec.isIgnoreJwt()) {
      String jwt = jwtGenerator.generate();
      headers.set("Authorization", "Bearer " + jwt);
    }

    HttpEntity<Object> request = new HttpEntity<Object>(headers);

    String url = spec.getUrl("/claims/" + claimId);
    return restTemplate.exchange(url, HttpMethod.GET, request, ClaimDetailResponse.class);
  }

  @SneakyThrows
  public ClaimDetail getClaimDetail(TestSpec spec) {
    ResponseEntity<ClaimDetailResponse> response = getClaim(spec);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ClaimDetailResponse body = response.getBody();
    return body.getClaim();
  }

  @SneakyThrows
  public ResponseEntity<ContentionSummariesResponse> getContentions(TestSpec spec) {
    final long claimId = spec.getClaimId();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (!spec.isIgnoreJwt()) {
      String jwt = jwtGenerator.generate();
      headers.set("Authorization", "Bearer " + jwt);
    }

    HttpEntity<Object> request = new HttpEntity<Object>(headers);

    String url = spec.getUrl("/claims/" + claimId + "/contentions");
    return restTemplate.exchange(url, HttpMethod.GET, request, ContentionSummariesResponse.class);
  }

  @SneakyThrows
  public ResponseEntity<UpdateContentionsResponse> putContentions(
      TestSpec spec, ExistingContention contention) {
    final long claimId = spec.getClaimId();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (!spec.isIgnoreJwt()) {
      String jwt = jwtGenerator.generate();
      headers.set("Authorization", "Bearer " + jwt);
    }

    var body = new UpdateContentionsRequest();
    body.addUpdateContentionsItem(contention);

    HttpEntity<UpdateContentionsRequest> request = new HttpEntity<>(body, headers);

    String url = spec.getUrl("/claims/" + claimId + "/contentions");
    return restTemplate.exchange(url, HttpMethod.PUT, request, UpdateContentionsResponse.class);
  }

  public List<ContentionSummary> getContentionSummaries(TestSpec spec) {
    ResponseEntity<ContentionSummariesResponse> response = getContentions(spec);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ContentionSummariesResponse csr = response.getBody();
    return csr.getContentions();
  }

  @SneakyThrows
  public ResponseEntity<ClaimLifecycleStatusesResponse> putLifecycleStatus(
      TestSpec spec, String value) {
    final long claimId = spec.getClaimId();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (!spec.isIgnoreJwt()) {
      String jwt = jwtGenerator.generate();
      headers.set("Authorization", "Bearer " + jwt);
    }

    var body = new UpdateClaimLifecycleStatusRequest();
    body.setClaimLifecycleStatus(value);

    HttpEntity<UpdateClaimLifecycleStatusRequest> request = new HttpEntity<>(body, headers);

    String url = spec.getUrl("/claims/" + claimId + "/lifecycle_status");
    return restTemplate.exchange(
        url, HttpMethod.PUT, request, ClaimLifecycleStatusesResponse.class);
  }
}
