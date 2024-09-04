package gov.va.vro.mockbipclaims.model.store;

import gov.va.vro.mockbipclaims.model.bip.SpecialIssueType;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class SpecialIssueTypesStore {
  Map<String, SpecialIssueType> store = new HashMap<>();

  /**
   * Retrieves all SpecialIssueTypes.
   *
   * @return Claim Store item
   */
  public SpecialIssueType[] all() {
    var values = store.values();
    return values.toArray(new SpecialIssueType[values.size()]);
  }

  /**
   * Retrieves the SpecialIssueType by code.
   *
   * @param code unique identifier of a special issue type
   * @return SpecialIssueType
   */
  public SpecialIssueType get(String code) {
    return store.get(code);
  }

  /**
   * Puts a new item in SpecialIssueType Store.
   *
   * @param item New SpecialIssueType to be stored
   */
  public void put(SpecialIssueType item) {
    var code = item.getCode();
    store.put(code, item);
  }
}
