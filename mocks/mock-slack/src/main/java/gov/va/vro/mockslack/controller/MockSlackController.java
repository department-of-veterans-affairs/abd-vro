package gov.va.vro.mockslack.controller;

import gov.va.vro.mockslack.api.MockSlackApi;
import gov.va.vro.mockslack.model.SlackMessage;
import gov.va.vro.mockslack.model.SlackMessageStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MockSlackController implements MockSlackApi {
  private final Pattern PATTERN = Pattern.compile("^.*collection id = (\\d{1,6}).*$");

  private final SlackMessageStore store;

  @Override
  public ResponseEntity<String> postSlackMessage(SlackMessage slackMessage) {
    log.info("posting slack message...");
    String text = slackMessage.getText();
    Matcher matcher = PATTERN.matcher(text);
    if (!matcher.find()) {
      String message = "No collection id found in message";
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
    String collectionIdAsString = matcher.group(1);
    log.info("collection id is extracted as: {}", collectionIdAsString);
    Integer collectionId = Integer.valueOf(collectionIdAsString, 10);
    store.put(collectionId, slackMessage);
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }

  @Override
  public ResponseEntity<SlackMessage> getSlackMessage(Integer collectionId) {
    log.info("retrieving slack message for collection {}...", collectionId);
    SlackMessage slackMessage = store.get(collectionId);
    if (slackMessage == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
    }
    return new ResponseEntity<>(slackMessage, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> deleteSlackMessage(Integer collectionId) {
    log.info("deleting slack message for collection {}...", collectionId);
    store.remove(collectionId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
