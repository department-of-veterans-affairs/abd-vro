package gov.va.vro.mockslack.model;

import java.util.HashMap;
import java.util.Map;

public class SlackMessageStore {
  private Map<Integer, SlackMessage> store = new HashMap<>();

  public SlackMessage put(Integer collectionId, SlackMessage message) {
    return store.put(collectionId, message);
  }

  public SlackMessage get(Integer collectionId) {
    return store.get(collectionId);
  }

  public void remove(Integer collectionId) { store.remove(collectionId); }
}
