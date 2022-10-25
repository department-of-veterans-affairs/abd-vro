package gov.va.vro.service.provider.mas.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.*;
import gov.va.vro.service.provider.MasApiProps;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class MasCollectionAnnotsApiService {
  private static final String BP_CONDITION = "Hypertension";
  private static final String ASTHMA_CONDITION = "Asthma";
  private static final String BP_SYSTOLIC_CODE = "8480-6";
  private static final String BP_SYSTOLIC_DISPLAY = "Systolic blood pressure";
  private static final String BP_DIASTOLIC_CODE = "8462-4";
  private static final String BP_DIASTOLIC_DISPLAY = "Diastolic blood pressure";
  private static final String BP_UNIT = "mm[Hg]";
  private static final String BP_READING_REGEX = "^\\d{1,3}\\/\\d{1,3}$";
  private static final String CLIENT_REGISTRATION_ID = "masAuthProvider";
  private static final String PRINCIPAL_NAME = "MAS Service";

  private AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager;

  @Autowired
  public void setMasAuthorizedClientServiceAndManager(
      AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager) {
    this.authorizedClientServiceAndManager = authorizedClientServiceAndManager;
  }

  /*
  private MasAuthToken masAuthToken;

  @Autowired
  public void setMasAuthToken(MasAuthToken masAuthToken) {
    this.masAuthToken = masAuthToken;
  }
  */

  private MasApiProps masApiProps;
  private RestTemplate restTemplate;

  public MasCollectionAnnotation getCollectionAnnots(Integer collectionId) throws MasException {
    try {
      // Build an OAuth2 request for the MAS Auth provider
      OAuth2AuthorizeRequest authorizeRequest =
          OAuth2AuthorizeRequest.withClientRegistrationId(CLIENT_REGISTRATION_ID)
              .principal(PRINCIPAL_NAME)
              .build();

      // Perform the actual authorization request using the authorized client service and authorized
      // client
      // manager. This is where the JWT is retrieved from the MAS Auth servers.
      OAuth2AuthorizedClient authorizedClient =
          authorizedClientServiceAndManager.authorize(authorizeRequest);

      // Get the token from the authorized client object
      OAuth2AccessToken accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      // headers.add("Authorization", "Bearer " + masAuthToken.getMasAuthToken());
      headers.add("Authorization", "Bearer " + accessToken);
      MasCollectionAnnotationReq masCollectionAnnotationReq = new MasCollectionAnnotationReq();
      masCollectionAnnotationReq.setCollectionsId(collectionId);
      HttpEntity<MasCollectionAnnotationReq> httpEntity =
          new HttpEntity<>(masCollectionAnnotationReq, headers);
      String url = masApiProps.getBaseURL() + masApiProps.getCollectionAnnotsPath();
      ResponseEntity<String> masResponse =
          restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
      String masResponseBody = masResponse.getBody();
      JsonFactory jsonFactory = new JsonFactory();
      ObjectMapper mapper = new ObjectMapper(jsonFactory);
      JsonNode rootNode = mapper.readTree(masResponseBody);
      return mapper.convertValue(rootNode.get(0), MasCollectionAnnotation.class);
    } catch (RestClientException | IOException e) {
      log.error("Failed to get collection annotations.", e);
      throw new MasException(e.getMessage(), e);
    }
  }

  public AbdEvidence mapAnnotsToEvidence(MasCollectionAnnotation masCollectionAnnotation) {

    AbdEvidence abdEvidence = new AbdEvidence();

    List<AbdMedication> medications = new ArrayList<>();
    List<AbdCondition> conditions = new ArrayList<>();
    List<AbdProcedure> procedures = new ArrayList<>();
    List<AbdBloodPressure> bpReadings = new ArrayList<>();
    boolean isConditionBP = false;
    boolean isConditionAsthma = false;

    for (MasDocument masDocument : masCollectionAnnotation.getDocuments()) {
      isConditionBP = masDocument.getCondition().equalsIgnoreCase(BP_CONDITION);
      isConditionAsthma = masDocument.getCondition().equalsIgnoreCase(ASTHMA_CONDITION);
      if (masDocument.getAnnotations() != null) {
        for (MasAnnotation masAnnotation : masDocument.getAnnotations()) {
          MasAnnotType AnnotationType =
              MasAnnotType.valueOf(masAnnotation.getAnnotType().toLowerCase());
          switch (AnnotationType) {
            case MEDICATION -> {
              AbdMedication abdMedication = new AbdMedication();
              abdMedication.setStatus(null);
              abdMedication.setNotes(null);
              abdMedication.setDescription(masAnnotation.getAnnotVal().toLowerCase());
              abdMedication.setRefills(-1);
              abdMedication.setAsthmaRelevant(null);
              abdMedication.setDuration(null);
              abdMedication.setAuthoredOn(masAnnotation.getObservationDate());
              abdMedication.setRoute(null);
              abdMedication.setAsthmaRelevant(isConditionAsthma);
              medications.add(abdMedication);
            }
            case CONDITION -> {
              AbdCondition abdCondition = new AbdCondition();
              abdCondition.setCode(masAnnotation.getAnnotVal());
              abdCondition.setText(masAnnotation.getAcdPrefName());
              abdCondition.setStatus(null);
              abdCondition.setAbatementDate(null);
              abdCondition.setOnsetDate(masAnnotation.getObservationDate());
            }
            case LABRESULT -> {
              if (isConditionBP && masAnnotation.getAnnotVal().matches(BP_READING_REGEX)) {
                String[] bpValues = masAnnotation.getAnnotVal().split("/");

                AbdBpMeasurement systolicReading = new AbdBpMeasurement();
                systolicReading.setCode(BP_SYSTOLIC_CODE);
                systolicReading.setDisplay(BP_SYSTOLIC_DISPLAY);
                systolicReading.setValue(
                    new BigDecimal(bpValues[0]).setScale(1, RoundingMode.HALF_UP));
                systolicReading.setUnit(BP_UNIT);

                AbdBpMeasurement diastolicReading = new AbdBpMeasurement();
                diastolicReading.setCode(BP_DIASTOLIC_CODE);
                diastolicReading.setDisplay(BP_DIASTOLIC_DISPLAY);
                diastolicReading.setValue(
                    new BigDecimal(bpValues[1]).setScale(1, RoundingMode.HALF_UP));
                diastolicReading.setUnit(BP_UNIT);

                AbdBloodPressure abdBloodPressure = new AbdBloodPressure();
                abdBloodPressure.setDate(masAnnotation.getObservationDate());
                abdBloodPressure.setSystolic(systolicReading);
                abdBloodPressure.setDiastolic(diastolicReading);
                abdBloodPressure.setOrganization(null);
                abdBloodPressure.setPractitioner(null);
                bpReadings.add(abdBloodPressure);
              }
            }
            case SERVICE, PROCEDURE -> { // NOP
            }
            default -> { // NOP
            }
          }
        }
      }
    }

    abdEvidence.setMedications(medications);
    abdEvidence.setConditions(conditions);
    abdEvidence.setProcedures(procedures);
    abdEvidence.setBloodPressures(bpReadings);

    return abdEvidence;
  }
}
