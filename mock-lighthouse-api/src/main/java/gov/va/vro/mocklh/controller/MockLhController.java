package gov.va.vro.mocklh.controller;

import gov.va.vro.mocklh.api.MockLhApi;
import gov.va.vro.mocklh.config.LhApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Map;

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

  private ResponseEntity<String> getResource(String bearerToken, MultiValueMap<String, String> queryParams, String resourceType) {
    log.info("Retrieving {}...", resourceType);

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set(HttpHeaders.AUTHORIZATION, bearerToken);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    String url = properties.getFhirUrl() + "/Observation";
    String fullUrl = UriComponentsBuilder.fromHttpUrl(url)
        .queryParams(queryParams)
        .build()
        .toUriString();

    log.info("Calling {}", url);
    ResponseEntity<String> response = template.exchange(fullUrl, HttpMethod.GET, entity, String.class);

    log.info("Observation response: {}", response.getBody());

    return response;
  }

  @Override
  public ResponseEntity<String> getObservation(String bearerToken, MultiValueMap<String, String> queryParams) {
    return getResource(bearerToken, queryParams, "Observation");
  }
}
