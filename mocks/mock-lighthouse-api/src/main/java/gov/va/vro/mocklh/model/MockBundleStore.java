package gov.va.vro.mocklh.model;

import java.util.HashMap;

public class MockBundleStore {
  private HashMap<String, MockBundles> store = new HashMap<>();

  public void put(String icn, MockBundles bundles) {
    store.put(icn, bundles);
  }

  /**
   * Get mock observation bundle.
   *
   * @param icn ICN veteran identifier
   * @return mock observation bundle
   */
  public String getMockObservationBundle(String icn) {
    MockBundles bundles = store.get(icn);
    if (bundles == null) {
      return null;
    }
    return bundles.getObservationBundle();
  }

  /**
   * Get mock condition bundle.
   *
   * @param icn ICN veteran identifier
   * @return mock condition bundle
   */
  public String getMockConditionBundle(String icn) {
    MockBundles bundles = store.get(icn);
    if (bundles == null) {
      return null;
    }
    return bundles.getConditionBundle();
  }

  /**
   * Get mock medication request bundle.
   *
   * @param icn ICN veteran identifier
   * @return mock medication request bundle
   */
  public String getMockMedicationRequestBundle(String icn) {
    MockBundles bundles = store.get(icn);
    if (bundles == null) {
      return null;
    }
    return bundles.getMedicationRequestBundle();
  }
}
