package gov.va.vro.service.provider.mas.service;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.rrd.mas.MasCollectionAnnotation;
import gov.va.vro.model.rrd.mas.MasCollectionStatus;
import gov.va.vro.model.rrd.mas.request.MasCollectionAnnotationRequest;
import gov.va.vro.model.rrd.mas.request.MasCollectionStatusRequest;
import gov.va.vro.model.rrd.mas.request.MasOrderExamRequest;
import gov.va.vro.service.provider.MasApiProps;
import gov.va.vro.service.provider.mas.MasException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
      String url = masApiProps.getBaseUrl() + masApiProps.getCollectionStatusPath();
      HttpHeaders headers = getMasHttpHeaders();
      List<MasCollectionStatusRequest> masCollectionStatusRequestList =
          collectionIds.stream().map(this::statusRequest).toList();

      HttpEntity<MasCollectionStatusRequest> httpEntity =
          new HttpEntity<>(masCollectionStatusRequestList.get(0), headers);

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

  private MasCollectionStatusRequest statusRequest(int collectionId) {
    MasCollectionStatusRequest masCollectionStatusRequest = new MasCollectionStatusRequest();
    masCollectionStatusRequest.setCollectionsId(collectionId);
    return masCollectionStatusRequest;
  }

  @Override
  public List<MasCollectionAnnotation> getCollectionAnnotations(Integer collectionId)
      throws MasException {
    try {
      String url = masApiProps.getBaseUrl() + masApiProps.getCollectionAnnotsPath();
      HttpHeaders headers = getMasHttpHeaders();

      MasCollectionAnnotationRequest masCollectionAnnotationRequest =
          new MasCollectionAnnotationRequest();
      masCollectionAnnotationRequest.setCollectionsId(collectionId);
      HttpEntity<MasCollectionAnnotationRequest> httpEntity =
          new HttpEntity<>(masCollectionAnnotationRequest, headers);

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
  public String orderExam(MasOrderExamRequest masOrderExamRequest) throws MasException {
    try {
      String url = masApiProps.getBaseUrl() + masApiProps.getCreateExamOrderPath();
      ObjectMapper mapper = new ObjectMapper();
      try {
        // convert user object to json string and return it
        log.info("masOrderExamReq JSON : " + mapper.writeValueAsString(masOrderExamRequest));
      } catch (JsonGenerationException | JsonMappingException e) {
        // catch various errors
        // NOP;
      }
      log.info(" Exam Order >>>> API Service URL : " + url);
      log.info(
          " Exam Order >>>> API Service collectionsid : "
              + masOrderExamRequest.getCollectionsId().toString());
      log.info(
          " Exam Order >>>> API Service condition text: "
              + masOrderExamRequest.getConditions().get(0).getContentionText());
      log.info(
          " Exam Order >>>> API Service condition code : "
              + masOrderExamRequest.getConditions().get(0).getConditionCode());
      HttpHeaders headers = getMasHttpHeaders();
      HttpEntity<MasOrderExamRequest> httpEntity = new HttpEntity<>(masOrderExamRequest, headers);

      ResponseEntity<String> masResponse =
          restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

      JsonNode masResponseJson = new ObjectMapper().readTree(String.valueOf(masResponse.getBody()));

      String examOrderRespStatus = "success";
      if ((isNull(masResponseJson.get("success")))) {
        examOrderRespStatus = "failed";
      }
      return examOrderRespStatus;
    } catch (RestClientException | IOException e) {
      log.error("Failed to order exam", e);
      // TODO: REPLACE WHEN FIXED
      //  Currently this MAS endpoint does not work, so mocking response in order to continue.
      // return "OK";
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
