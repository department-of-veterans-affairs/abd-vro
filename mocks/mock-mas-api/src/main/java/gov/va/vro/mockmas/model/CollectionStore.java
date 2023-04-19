package gov.va.vro.mockmas.model;

import gov.va.vro.model.rrd.mas.MasCollectionAnnotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionStore {
  private Map<Integer, List<MasCollectionAnnotation>> store = new HashMap<>();

  public void put(Integer collectionId, List<MasCollectionAnnotation> collection) {
    store.put(collectionId, collection);
  }

  public List<MasCollectionAnnotation> get(Integer collectionId) {
    return store.get(collectionId);
  }
}
