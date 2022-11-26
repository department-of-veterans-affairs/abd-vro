package gov.va.vro.controller;

import gov.va.vro.BaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public abstract class BaseControllerTest extends BaseIntegrationTest {

  @Autowired protected TestRestTemplate testRestTemplate;

  protected <I, O> ResponseEntity<O> post(String url, I request, Class<O> responseType) {
    return exchange(url, request, HttpMethod.POST, responseType);
  }

  protected <I, O> ResponseEntity<O> get(String url, I request, Class<O> responseType) {
    return exchange(url, request, HttpMethod.GET, responseType);
  }

  protected <I, O> ResponseEntity<O> get(
      String url, I request, Map<String, String> headers, Class<O> responseType) {
    return exchange(url, request, HttpMethod.GET, headers, responseType);
  }

  protected <I, O> ResponseEntity<O> exchange(
      String url,
      I request,
      HttpMethod method,
      Map<String, String> headersIn,
      Class<O> responseType) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-Key", "test-key-01");
    if (headersIn != null) {
      headersIn.forEach(
          (key, value) -> {
            headers.add(key, value);
          });
    }
    var httpEntity = new HttpEntity<>(request, headers);
    return testRestTemplate.exchange(url, method, httpEntity, responseType);
  }

  protected <I, O> ResponseEntity<O> exchange(
      String url, I request, HttpMethod method, Class<O> responseType) {
    return exchange(url, request, method, null, responseType);
  }
}
