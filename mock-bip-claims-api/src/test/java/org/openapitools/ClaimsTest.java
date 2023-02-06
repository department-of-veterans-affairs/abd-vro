package org.openapitools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.openapitools.configuration.TestConfig;
import org.openapitools.model.ClaimDetail;
import org.openapitools.model.ClaimDetailResponse;
import org.openapitools.model.ContentionSummariesResponse;
import org.openapitools.model.ContentionSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Import(TestConfig.class)
@ActiveProfiles("test")
public class ClaimsTest {
  @LocalServerPort int port;

  @Autowired
  @Qualifier("httpsRestTemplate")
  private RestTemplate restTemplate;

  private String getUrl(String endPoint) {
    String base = "https://localhost:" + port + "/";
    return base + endPoint;
  }

  @Test
  void happyPathTest() {
    String baseUrl = getUrl("/claims/");
    String url = baseUrl + 1010;
    ResponseEntity<ClaimDetailResponse> response =
        restTemplate.getForEntity(url, ClaimDetailResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ClaimDetailResponse body = response.getBody();
    ClaimDetail claim = body.getClaim();
    String tempStationOfJurisdiction = claim.getTempStationOfJurisdiction();
    assertEquals("398", tempStationOfJurisdiction);

    String urlContentions = url + "/contentions";
    ResponseEntity<ContentionSummariesResponse> response2 =
        restTemplate.getForEntity(urlContentions, ContentionSummariesResponse.class);
    assertEquals(HttpStatus.OK, response2.getStatusCode());
    ContentionSummariesResponse csr = response2.getBody();
    List<ContentionSummary> contentions = csr.getContentions();
    assertEquals(1, contentions.size());
    ContentionSummary summary = contentions.get(0);
    var codes = summary.getSpecialIssueCodes();
    assertTrue(codes.indexOf("rrd") >= 0);
    assertTrue(codes.indexOf("AOOV") >= 0);
  }
}
