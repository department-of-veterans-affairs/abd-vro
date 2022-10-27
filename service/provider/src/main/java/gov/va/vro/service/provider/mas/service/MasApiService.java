package gov.va.vro.service.provider.mas.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.service.provider.MasApiProps;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasApiService {
  private final RestTemplate restTemplate;
  private final MasAuthToken masAuthToken;
  private final MasApiProps masApiProps;

  public List<MasCollectionStatus> getMasCollectionStatus(List<Integer> collectionIds)
      throws MasException {
    try {
      String url = masApiProps.getBaseURL() + masApiProps.getCollectionStatusPath();
      HttpHeaders headers = getMasHttpHeaders();
      List<MasCollectionStatusReq> masCollectionStatusReqList =
          new ArrayList<MasCollectionStatusReq>(collectionIds.size());
      for (Integer collectionId : collectionIds) {
        MasCollectionStatusReq masCollectionStatusReq = new MasCollectionStatusReq();
        masCollectionStatusReq.setCollectionsId(collectionId);
        masCollectionStatusReqList.add(masCollectionStatusReq);
      }
      HttpEntity<MasCollectionStatusReq> httpEntity =
          new HttpEntity<>(masCollectionStatusReqList.get(0), headers);

      ResponseEntity<String> masResponse =
          restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
      log.info("Call {} to get MAS collection status.", url);
      log.info("MAS Collection Status Response {}.", masResponse);

      String masReturn = masResponse.getBody();
      ObjectMapper mapper = new ObjectMapper();

      log.info("MAS Collection Status Response body {}.", masReturn);
      return mapper.readValue(masReturn, new TypeReference<List<MasCollectionStatus>>() {});
    } catch (RestClientException | IOException e) {
      log.error("Failed to get collection status.", e);
      throw new MasException(e.getMessage(), e);
    }
  }

  public List<MasCollectionAnnotation> getCollectionAnnots(Integer collectionId)
      throws MasException {
    try {
      String url = masApiProps.getBaseURL() + masApiProps.getCollectionAnnotsPath();
      HttpHeaders headers = getMasHttpHeaders();

      MasCollectionAnnotationReq masCollectionAnnotationReq = new MasCollectionAnnotationReq();
      masCollectionAnnotationReq.setCollectionsId(collectionId);
      HttpEntity<MasCollectionAnnotationReq> httpEntity =
          new HttpEntity<>(masCollectionAnnotationReq, headers);

      ResponseEntity<String> masResponse =
          restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

      String masReturn = masResponse.getBody();
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(masReturn, new TypeReference<List<MasCollectionAnnotation>>() {});
      /*
            String masResponseBody = masResponse.getBody();
            JsonFactory jsonFactory = new JsonFactory();
            ObjectMapper mapper = new ObjectMapper(jsonFactory);
            JsonNode rootNode = mapper.readTree(masResponseBody);

            return mapper.convertValue(rootNode.get(0), MasCollectionAnnotation.class);
      */
    } catch (RestClientException | IOException e) {
      log.error("Failed to get collection annotations.", e);
      throw new MasException(e.getMessage(), e);
    }
  }

  public String orderExam(MasOrderExamReq masOrderExamReq) throws MasException {
    try {
      String url = masApiProps.getBaseURL() + masApiProps.getCreateExamOrderPath();
      HttpHeaders headers = getMasHttpHeaders();

      HttpEntity<MasOrderExamReq> httpEntity = new HttpEntity<>(masOrderExamReq, headers);

      ResponseEntity<String> masResponse =
          restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

      String masReturn = masResponse.getBody();

      // Replace with the actual response contract when available
      // ObjectMapper mapper = new ObjectMapper();
      // return mapper.readValue(masReturn, new TypeReference<List<MasOrderExam>>() {});

      return masReturn;
    } catch (RestClientException e) {
      log.error("Failed to order exam", e);
      throw new MasException(e.getMessage(), e);
    }
  }

  public HttpHeaders getMasHttpHeaders() throws MasException {
    HttpHeaders masHttpHeaders;
    try {
      // Get the MAS API Auth(JWT) Token
      OAuth2AccessToken accessToken = masAuthToken.getMasApiAuthToken();

      masHttpHeaders = new HttpHeaders();
      masHttpHeaders.setContentType(MediaType.APPLICATION_JSON);
      masHttpHeaders.add("Authorization", "Bearer " + accessToken.getTokenValue());

    } catch (Exception e) {
      log.error("Failed to build MAS HTTP Headers.", e);
      throw new MasException(e.getMessage(), e);
    }
    return masHttpHeaders;
  }

  public RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public MasAuthToken getMasAuthToken() {
    return masAuthToken;
  }
}
