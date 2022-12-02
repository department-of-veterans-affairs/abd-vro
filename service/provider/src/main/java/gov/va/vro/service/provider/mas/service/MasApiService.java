package gov.va.vro.service.provider.mas.service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.mas.*;
import gov.va.vro.service.provider.MasApiProps;
import gov.va.vro.service.provider.mas.MasException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasApiService implements IMasApiService {

  private final ObjectMapper mapper = new ObjectMapper();
  private final RestTemplate restTemplate;
  private final MasAuthToken masAuthToken;
  private final MasApiProps masApiProps;

  @Override
  public List<MasCollectionStatus> getMasCollectionStatus(List<Integer> collectionIds)
      throws MasException {
    try {
      String url = masApiProps.getBaseURL() + masApiProps.getCollectionStatusPath();
      HttpHeaders headers = getMasHttpHeaders();
      List<MasCollectionStatusReq> masCollectionStatusReqList =
          collectionIds.stream().map(this::statusRequest).toList();

      HttpEntity<MasCollectionStatusReq> httpEntity =
          new HttpEntity<>(masCollectionStatusReqList.get(0), headers);

      ResponseEntity<String> masResponse =
          restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
      log.info("Call {} to get MAS collection status.", url);
      log.info("MAS Collection Status Response Status {}.", masResponse.getStatusCode());

      String masReturn = masResponse.getBody();

      log.trace("MAS Collection Status Response body {}.", masReturn);
      return mapper.readValue(masReturn, new TypeReference<>() {});
    } catch (RestClientException | IOException e) {
      log.error("Failed to get collection status.", e);
      throw new MasException(e.getMessage(), e);
    }
  }

  private MasCollectionStatusReq statusRequest(int collectionId) {
    MasCollectionStatusReq masCollectionStatusReq = new MasCollectionStatusReq();
    masCollectionStatusReq.setCollectionsId(collectionId);
    return masCollectionStatusReq;
  }

  @Override
  public List<MasCollectionAnnotation> getCollectionAnnotations(Integer collectionId)
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
      return mapper.readValue(masReturn, new TypeReference<>() {});
    } catch (RestClientException | IOException e) {
      log.error("Failed to get collection annotations.", e);
      throw new MasException(e.getMessage(), e);
    }
  }

  @Override
  public String orderExam(MasOrderExamReq masOrderExamReq) throws MasException {
    try {
      String url = masApiProps.getBaseURL() + masApiProps.getCreateExamOrderPath();
      HttpHeaders headers = getMasHttpHeaders();
      ObjectMapper mapper = new ObjectMapper();

      try {
        // convert user object to json string and return it
        log.info("masOrderExamReq JSON : " + mapper.writeValueAsString(masOrderExamReq));
      } catch (JsonGenerationException | JsonMappingException e) {
        // catch various errors
        // NOP;
      }
      log.info(" Exam Order >>>> API Service URL : " + url);
      log.info(
          " Exam Order >>>> API Service collectionsid : "
              + masOrderExamReq.getCollectionsId().toString());
      log.info(
          " Exam Order >>>> API Service condition text: "
              + masOrderExamReq.getConditions().get(0).getContentionText());
      log.info(
          " Exam Order >>>> API Service condition code : "
              + masOrderExamReq.getConditions().get(0).getConditionCode());

      HttpEntity<MasOrderExamReq> httpEntity = new HttpEntity<>(masOrderExamReq, headers);

      ResponseEntity<String> masResponse =
          restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
      return mapper.readValue(masResponse.getBody(), new TypeReference<>() {});

    } catch (RestClientException | IOException e) {
      log.error("Failed to order exam", e);
      throw new MasException(e.getMessage(), e);
    }
  }

  private HttpHeaders getMasHttpHeaders() throws MasException {
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
}
