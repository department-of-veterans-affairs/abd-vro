package gov.va.vro.service.provider.mas.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.model.MasCollectionStatus;
import gov.va.vro.service.provider.mas.model.MasOrderExam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * It implements the service to access MAS API.
 *
 * @author warren @Date 10/5/22
 */
@Slf4j
@Service
@AllArgsConstructor
public class MasApiService {

  // TODO: When MAS API connection information is available, externalize the settings.
  private static final String MAS_URL = "http://localhost:5000/pca/api/dev/";
  private static final String COLLECTION_STATUS = "pcCheckCollectionStatus";
  private static final String ORDER_EXAM = "pcOrderExam";

  private final RestTemplate restTemplate;

  public List<MasCollectionStatus> getMasCollectionStatus(List<Integer> collectionIds)
      throws MasException {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<String> httpEntity = new HttpEntity<>(headers);
      String url = MAS_URL + COLLECTION_STATUS;
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(new URI(url));
      uriBuilder.queryParam("Collection Identifiers", collectionIds);
      log.info("Call {} to get MAS collection status.", uriBuilder.toUriString());
      ResponseEntity<String> status =
          restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, httpEntity, String.class);
      String masReturn = status.getBody();
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(masReturn, new TypeReference<List<MasCollectionStatus>>() {});
    } catch (RestClientException | URISyntaxException | IOException e) {
      log.error("Failed to get collection status.", e);
      throw new MasException(e.getMessage(), e);
    }
  }

  public MasOrderExam orderExam(int collectionId) throws MasException {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("collectionId", collectionId + "");
      HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(requestBody, headers);
      String url = MAS_URL + ORDER_EXAM;
      log.info("Call {} to order MAS medical exam for {}.", url, collectionId);
      ResponseEntity<String> medicalOrder =
          restTemplate.postForEntity(url, httpEntity, String.class);
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(medicalOrder.getBody(), MasOrderExam.class);
    } catch (RestClientException | IOException e) {
      log.error("call MAS to order medical exam failed for {}.", collectionId, e);
      throw new MasException("Failed to order a medical exam.", e);
    }
  }
}
