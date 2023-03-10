package gov.va.vro.mockmas.model;

import java.util.HashMap;
import java.util.Map;

public class ExamOrderStore {
  private Map<Integer, Boolean> store = new HashMap<>();

  public void put(Integer collectionId, Boolean orderedExam) {
    store.put(collectionId, orderedExam);
  }

  public Boolean get(Integer collectionId) {
    return store.get(collectionId);
  }

  public void reset(Integer collectionId) {
    store.remove(collectionId);
  }
}
