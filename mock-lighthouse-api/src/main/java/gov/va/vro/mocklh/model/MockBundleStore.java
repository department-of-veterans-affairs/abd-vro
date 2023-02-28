package gov.va.vro.mocklh.model;

import java.util.HashMap;

public class MockBundleStore {
  private HashMap<String, MockBundles> store = new HashMap<>();

  public void put(String icn, MockBundles bundles) {
    store.put(icn, bundles);
  }

  public String getMockObservationBundle(String icn) {
    MockBundles bundles = store.get(icn);
    if (bundles == null) {
      return null;
    }
    return bundles.getObservationBundle();
  }

  public String getMockConditionBundle(String icn) {
    MockBundles bundles = store.get(icn);
    if (bundles == null) {
      return null;
    }
    return bundles.getConditionBundle();
  }

  public String getMockMedicationRequestBundle(String icn) {
    MockBundles bundles = store.get(icn);
    if (bundles == null) {
      return null;
    }
    return bundles.getMedicationRequestBundle();
  }
}
