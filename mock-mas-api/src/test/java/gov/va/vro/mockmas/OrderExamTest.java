package gov.va.vro.mockmas;

import gov.va.vro.mockmas.config.MasApiProperties;
import gov.va.vro.mockmas.config.MasOauth2Properties;
import gov.va.vro.mockmas.model.OrderExamRequest;
import gov.va.vro.mockmas.model.OrderExamResponse;
import gov.va.vro.mockmas.model.OrderExamSuccess;
import gov.va.vro.model.mas.MasCollectionAnnotation;
import gov.va.vro.model.mas.request.MasCollectionAnnotationRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("test")
@EnableConfigurationProperties({MasApiProperties.class, MasOauth2Properties.class})
public class OrderExamTest {
  @LocalServerPort private int port;

  @Autowired private RestTemplate template;

  @Autowired private MasApiProperties apiProperties;

  private void sanityCheck(int collectionId, int collectionLength, int documentSize) {
 }

  @Test
  void orderExam375Test() {
    String url = "http://localhost:" + port + apiProperties.getCreateExamOrderPath();
    OrderExamRequest request = new OrderExamRequest();
    request.setCollectionsId(375);

    ResponseEntity<OrderExamResponse> response =
        template.postForEntity(url, request, OrderExamResponse.class);
    OrderExamResponse body = response.getBody();
    OrderExamSuccess success = body.getSuccess();

    assertEquals(375, success.getCollectionsId());
  }
}
