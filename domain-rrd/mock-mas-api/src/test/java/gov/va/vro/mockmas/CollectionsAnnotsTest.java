package gov.va.vro.mockmas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.mockmas.config.MasApiProperties;
import gov.va.vro.mockmas.config.MasOauth2Properties;
import gov.va.vro.model.rrd.mas.MasCollectionAnnotation;
import gov.va.vro.model.rrd.mas.request.MasCollectionAnnotationRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("test")
@EnableConfigurationProperties({MasApiProperties.class, MasOauth2Properties.class})
public class CollectionsAnnotsTest {
  @LocalServerPort private int port;

  @Autowired private RestTemplate template;

  @Autowired private MasApiProperties apiProperties;

  private void sanityCheck(int collectionId, int collectionLength, int documentSize) {
    String url = "http://localhost:" + port + apiProperties.getCollectionAnnotsPath();
    MasCollectionAnnotationRequest request = new MasCollectionAnnotationRequest();
    request.setCollectionsId(collectionId);

    ResponseEntity<MasCollectionAnnotation[]> response =
        template.postForEntity(url, request, MasCollectionAnnotation[].class);
    MasCollectionAnnotation[] body = response.getBody();

    assertEquals(collectionLength, body.length);

    MasCollectionAnnotation collection = body[0];
    assertEquals(documentSize, collection.getDocuments().size());
  }

  @Test
  void collection375Test() {
    sanityCheck(375, 1, 2);
  }
}
