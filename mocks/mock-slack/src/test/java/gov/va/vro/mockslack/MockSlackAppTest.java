package gov.va.vro.mockslack;

import gov.va.vro.mockslack.model.SlackMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("test")
public class MockSlackAppTest {
  private final RestTemplate template = new RestTemplate();

  @LocalServerPort
  private int port;

  private String message(int collectionId, String diagnosticCode, String actionType) {
    String p1 = String.format("[collection id = %s]", collectionId);
    String p2 = String.format("[diagnostic code = %s]", diagnosticCode);
    String p3 = String.format("[disability action type = %s]", actionType);
    return "Claim with " + p1 + ", " + p2 + ", and " + p3 + " is not in scope";
  }

  @Test
  void postGetDeleteTest() {
    int collectionId = 350;

    String url = "http://localhost:" + port + "/slack-messages";
    String text = message(collectionId, "7101", "DECREASE");

    SlackMessage slackMessage = new SlackMessage(text);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<SlackMessage> entity = new HttpEntity<>(slackMessage, headers);

    ResponseEntity<String> response = template.postForEntity(url, entity, String.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("ok", response.getBody());

    String getUrl = url + "/" + collectionId;
    ResponseEntity<SlackMessage> getResponse = template.getForEntity(getUrl, SlackMessage.class);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    SlackMessage actualSlackMessage = getResponse.getBody();
    assertNotNull(actualSlackMessage);
    assertEquals(text, actualSlackMessage.getText());

    String delUrl = url + "/" + collectionId;
    template.delete(delUrl);

    try {
      template.getForEntity(getUrl, SlackMessage.class);
      fail("Should have gotten 404");
    } catch (HttpStatusCodeException exception) {
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
  }
}
