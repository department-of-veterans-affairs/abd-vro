package gov.va.vro.mockslack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import gov.va.vro.mockslack.model.SlackMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("test")
public class MockSlackAppTest {
  private final RestTemplate template = new RestTemplate();

  @Autowired private Pattern collectionIdExtractor;

  @LocalServerPort private int port;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/slack-messages";
  }

  private String message(int collectionId, String diagnosticCode, String actionType) {
    String p1 = String.format("[collection id = %s]", collectionId);
    String p2 = String.format("[diagnostic code = %s]", diagnosticCode);
    String p3 = String.format("[disability action type = %s]", actionType);
    return "Claim with " + p1 + ", " + p2 + ", and " + p3 + " is not in scope";
  }

  private ResponseEntity<String> postSlackMessage(String text) {
    SlackMessage slackMessage = new SlackMessage(text);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<SlackMessage> entity = new HttpEntity<>(slackMessage, headers);

    return template.postForEntity(getBaseUrl(), entity, String.class);
  }

  @Test
  void postGetDeleteTest() {
    int collectionId = 350;
    String text = message(collectionId, "7101", "DECREASE");

    ResponseEntity<String> response = postSlackMessage(text);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("ok", response.getBody());

    String url = getBaseUrl() + "/" + collectionId;
    ResponseEntity<SlackMessage> getResponse = template.getForEntity(url, SlackMessage.class);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    SlackMessage actualSlackMessage = getResponse.getBody();
    assertNotNull(actualSlackMessage);
    assertEquals(text, actualSlackMessage.getText());

    template.delete(url);

    try {
      template.getForEntity(url, SlackMessage.class);
      fail("Should have gotten 404");
    } catch (HttpStatusCodeException exception) {
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
  }

  @Test
  void collectionIdNotANumberTest() {
    try {
      postSlackMessage("the collection id = not a number");
      fail("Expected a bad request");
    } catch (HttpStatusCodeException exception) {
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
  }

  private String findCollectionId(String text) {
    Matcher matcher = collectionIdExtractor.matcher(text);
    assertTrue(matcher.find());
    return matcher.group(1);
  }

  @Test
  void testPattern() {
    String result1 = findCollectionId("XXXDDFFFE collection id = 745 jdujejj");
    assertEquals("745", result1);

    String result2 = findCollectionId("XXXDDFFFE collection ID = 745 jdujejj");
    assertEquals("745", result2);

    String result3 = findCollectionId("XXXDDFFFE collection ID: 745");
    assertEquals("745", result3);
  }
}
