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

  protected <I, O> ResponseEntity<O> post(
      String url, I request, Map<String, String> headers, Class<O> responseType) {
    return exchange(url, request, HttpMethod.POST, headers, responseType);
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
    headers.add("Authorization", "Bearer " + sampleJWT);
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

  String sampleJWT =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImMwOTI5NTJlLTM4ZDYtNDNjNi05MzBlLWZmOTNiYTUxYjA4ZiJ9.eyJleHAiOjk5OTk5OTk5OTksImlhdCI6MTY0MTA2Nzk0OSwianRpIjoiNzEwOTAyMGEtMzlkOS00MWE4LThlNzgtNTllZjAwYTlkNDJlIiwiaXNzIjoiaHR0cHM6Ly9zYW5kYm94LWFwaS52YS5nb3YvaW50ZXJuYWwvYXV0aC92Mi92YWxpZGF0aW9uIiwiYXVkIjoibWFzX2RldiIsInN1YiI6IjhjNDkyY2NmLTk0OGYtNDQ1Zi05NmY4LTMxZTdmODU5MDlkMiIsInR5cCI6IkJlYXJlciIsImF6cCI6Im1hc19kZXYiLCJzY29wZSI6Im9wZW5pZCB2cm9fbWFzIiwiY2xpZW50SWQiOiJtYXNfZGV2In0.Qb41CR1JIGGRlryi-XVtqyeNW73cU1YeBVqs9Bps3TA";
}
