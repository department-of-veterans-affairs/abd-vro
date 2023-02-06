package gov.va.vro.mockbipclaims.util;

import gov.va.vro.mockbipclaims.model.ClaimDetailResponse;
import gov.va.vro.mockbipclaims.model.ContentionSummariesResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
}
