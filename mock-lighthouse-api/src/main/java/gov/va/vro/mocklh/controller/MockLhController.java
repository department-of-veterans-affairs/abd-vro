package gov.va.vro.mocklh.controller;

import gov.va.vro.mocklh.api.MockLhApi;
import gov.va.vro.mocklh.config.LhApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MockLhController implements MockLhApi {
  private final RestTemplate template;

  private final LhApiProperties properties;

  @Override
  public ResponseEntity<String> getToken(MultiValueMap<String, String> requestBody) {
    log.info("Retrieving token...");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<MultiValueMap<String, String>> newRequest = new HttpEntity<>(requestBody, headers);

    String url = properties.getTokenUrl();
    ResponseEntity<String> response = template.postForEntity(url, newRequest, String.class);

    log.info("token response: {}", response.getBody());

    return response;
  }
}
