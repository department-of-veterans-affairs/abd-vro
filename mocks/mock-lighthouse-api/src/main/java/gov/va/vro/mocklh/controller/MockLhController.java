package gov.va.vro.mocklh.controller;

import gov.va.vro.mocklh.api.MockLhApi;
import gov.va.vro.mocklh.config.LhApiProperties;
import gov.va.vro.mocklh.model.MockBundleStore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MockLhController implements MockLhApi {
  private final RestTemplate template;

  private final LhApiProperties properties;

  private final MockBundleStore store;

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

  private ResponseEntity<String> getResource(
      String bearerToken, MultiValueMap<String, String> queryParams, String resourceType) {
    log.info("Retrieving {}...", resourceType);

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set(HttpHeaders.AUTHORIZATION, bearerToken);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    String url = properties.getFhirUrl() + "/" + resourceType;
    String fullUrl =
        UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams).build().toUriString();

    log.info("Calling {}", url);
    ResponseEntity<String> response =
        template.exchange(fullUrl, HttpMethod.GET, entity, String.class);

    log.info("{} response: {}", resourceType, response.getBody());

    return response;
  }

  @Override
  @SneakyThrows
  public ResponseEntity<String> getObservation(
      String bearerToken, MultiValueMap<String, String> queryParams) {
    String icn = queryParams.getFirst("patient");

    if (icn.equals("mock1012666073V986365")) { // icn for 500 exception test
      log.info("Raising error for Observation: {}", icn);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Expected exception for testing");
    }

    if (icn.equals("mock1012666073V986366")) { // icn for timeout exception test
      log.info("Waiting to cause timeout: {}", icn);
      Thread.sleep(125000); // With a bit more than 2 minutes for timeout
    }

    String bundle = store.getMockObservationBundle(icn);
    if (bundle != null) {
      log.info("Sending back the mock Observation: {}", icn);
      return new ResponseEntity<>(bundle, HttpStatus.OK);
    }
    return getResource(bearerToken, queryParams, "Observation");
  }

  @Override
  public ResponseEntity<String> getCondition(
      String bearerToken, MultiValueMap<String, String> queryParams) {
    String icn = queryParams.getFirst("patient");
    String bundle = store.getMockConditionBundle(icn);
    if (bundle != null) {
      log.info("Sending back the mock Condition: {}", icn);
      return new ResponseEntity<>(bundle, HttpStatus.OK);
    }
    return getResource(bearerToken, queryParams, "Condition");
  }

  @Override
  public ResponseEntity<String> getMedicationRequest(
      String bearerToken, MultiValueMap<String, String> queryParams) {
    String icn = queryParams.getFirst("patient");
    String bundle = store.getMockMedicationRequestBundle(icn);
    if (bundle != null) {
      log.info("Sending back the mock MedicationRequest: {}", icn);
      return new ResponseEntity<>(bundle, HttpStatus.OK);
    }
    return getResource(bearerToken, queryParams, "MedicationRequest");
  }
}
