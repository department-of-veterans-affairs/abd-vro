package gov.va.vro.abddataaccess.service;

import static org.junit.jupiter.api.Assertions.*;

import gov.va.vro.abddataaccess.model.MasCollectionAnnotation;
import gov.va.vro.abddataaccess.model.MasCollectionStatus;
import gov.va.vro.abddataaccess.model.MasDocument;
import gov.va.vro.abddataaccess.model.MasOrderExam;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Unit tests for MasApiService.
 *
 * @author warren @Date 10/11/22
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
@ActiveProfiles("test")
class MasApiServiceImplTest {
  private static final String MAS_STATUS = "mas-getstatus-response.json";
  private static final String MAS_ANNOTATIONS = "mas-annotation-response.json";
  private static final String MAS_ORDEREXAM = "mas-orderexam-response.json";

  private static final String MAS_URL = "http://localhost:5000/pca/api/dev/";
  private static final String COLLECTION_STATUS = "pcCheckCollectionStatus";
  private static final String COLLECTION_ANNOTATIONS = "pcQueryCollectionAnnots";
  private static final String ORDER_EXAM = "pcOrderExam";

  private static final int TEST_ID = 1234;

  @InjectMocks private MasApiServiceImpl service;

  @Mock private RestTemplate restTemplate;

  private HttpHeaders headers;

  @BeforeEach
  void setUp() {
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
  }

  private void setupMockingMasGetStatus() {
    try {
      String filename =
          Objects.requireNonNull(getClass().getClassLoader().getResource(MAS_STATUS)).getPath();
      Path filePath = Path.of(filename);
      String statusResp = Files.readString(filePath);

      HttpEntity<String> httpEntity = new HttpEntity<>(headers);
      String url = MAS_URL + COLLECTION_STATUS;
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(new URI(url));
      uriBuilder.queryParam("Collection Identifiers", TEST_ID);
      ResponseEntity<String> resp = ResponseEntity.ok(statusResp);
      Mockito.doReturn(resp)
          .when(restTemplate)
          .exchange(uriBuilder.toUriString(), HttpMethod.GET, httpEntity, String.class);
    } catch (NullPointerException | IOException | URISyntaxException e) {
      log.error("Failed to get test file path.", e);
      fail("Mocking");
    }
  }

  private void setupMockMasGetAnnotation() {
    try {
      String filename =
          Objects.requireNonNull(getClass().getClassLoader().getResource(MAS_ANNOTATIONS))
              .getPath();
      Path filePath = Path.of(filename);
      String annotResp = Files.readString(filePath);

      HttpEntity<String> httpEntity = new HttpEntity<>(headers);
      String url = MAS_URL + COLLECTION_ANNOTATIONS;
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(new URI(url));
      uriBuilder.queryParam("collectionId", TEST_ID);
      ResponseEntity<String> resp = ResponseEntity.ok(annotResp);
      Mockito.doReturn(resp)
          .when(restTemplate)
          .exchange(uriBuilder.toUriString(), HttpMethod.GET, httpEntity, String.class);
    } catch (NullPointerException | IOException | URISyntaxException e) {
      log.error("Failed to get test file path.", e);
      fail("Mocking");
    }
  }

  private void setupMockMasOrderExam() {
    try {
      String filename =
          Objects.requireNonNull(getClass().getClassLoader().getResource(MAS_ORDEREXAM)).getPath();
      Path filePath = Path.of(filename);
      String examResp = Files.readString(filePath);

      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("collectionId", TEST_ID + "");
      HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(requestBody, headers);
      String url = MAS_URL + ORDER_EXAM;
      ResponseEntity<String> resp = ResponseEntity.ok(examResp);
      Mockito.doReturn(resp).when(restTemplate).postForEntity(url, httpEntity, String.class);
    } catch (NullPointerException | IOException e) {
      log.error("Failed to get test file path.", e);
      fail("Mocking");
    }
  }

  @Test
  public void testGetMasCollectionStatus() {
    try {
      setupMockingMasGetStatus();
      List<MasCollectionStatus> resp =
          service.getMasCollectionStatus(Collections.singletonList(TEST_ID));
      assertEquals(1, resp.size());
    } catch (Exception e) {
      log.error("testGetMasCollectionStatus failed.", e);
      fail("testGetMasCollectionStatus");
    }
  }

  @Test
  public void testQueryCollectionAnnots() {
    try {
      setupMockMasGetAnnotation();
      List<MasCollectionAnnotation> resp = service.queryCollectionAnnots(TEST_ID);
      assertEquals(1, resp.size());
      MasCollectionAnnotation collectionAnnotation = resp.get(0);
      assertTrue(collectionAnnotation.getVtrnFileId() > 0);
      assertFalse(collectionAnnotation.getCreationDate().isEmpty());
      assertEquals(1, collectionAnnotation.getDocuments().size());
      MasDocument document = collectionAnnotation.getDocuments().get(0);
      assertTrue(document.getEfolderversionrefid() > 0);
      assertFalse(document.getCondition().isEmpty());
      assertEquals(1, document.getAnnotations().size());
    } catch (Exception e) {
      log.error("testQueryCollectionAnnots failed.", e);
      fail("testQueryCollectionAnnots");
    }
  }

  @Test
  public void testOrderExam() {
    try {
      setupMockMasOrderExam();
      MasOrderExam resp = service.orderExam(TEST_ID);
      assertFalse(resp.getStatus().isEmpty());
    } catch (Exception e) {
      log.error("testOrderExam failed.", e);
      fail("testOrderExam");
    }
  }
}