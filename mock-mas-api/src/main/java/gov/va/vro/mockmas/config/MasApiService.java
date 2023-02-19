package gov.va.vro.mockmas.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mockmas.model.MasTokenResponse;
import gov.va.vro.model.mas.MasCollectionAnnotation;
import gov.va.vro.model.mas.request.MasCollectionAnnotationRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasApiService {
  private final RestTemplate template;
  private final ObjectMapper mapper;
  private final MasApiProperties apiProperties;
  private final MasOauth2Properties oauth2Properties;

  public MasTokenResponse getToken() {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    final String url = oauth2Properties.getTokenUri();

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("scope", oauth2Properties.getScope());
    map.add("grant_type", oauth2Properties.getGrantType());
    map.add("client_id", oauth2Properties.getClientId());
    map.add("client_secret", oauth2Properties.getClientSecret());

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

    var response = template.postForEntity(url, request, MasTokenResponse.class);
    return response.getBody();
  }

  @SneakyThrows
  public List<MasCollectionAnnotation> getAnnotation(int collectionId) {
    MasTokenResponse tokenResponse = getToken();
    String token = tokenResponse.getAccessToken();

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(token);

    String baseUrl = apiProperties.getBaseUrl();
    final String url = baseUrl + apiProperties.getCollectionAnnotsPath();

    MasCollectionAnnotationRequest body = new MasCollectionAnnotationRequest();
    body.setCollectionsId(collectionId);

    HttpEntity<MasCollectionAnnotationRequest> request = new HttpEntity<>(body, headers);

    var response = template.postForEntity(url, request, String.class);
    String responseBody = response.getBody();
    return mapper.readValue(responseBody, new TypeReference<>() {});
  }
}
