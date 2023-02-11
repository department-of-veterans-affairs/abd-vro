package gov.va.vro.mockbipclaims.config;

import gov.va.vro.mockbipclaims.model.ClaimDetail;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class ClaimStore {
  Map<String, ClaimStoreItem> store = new HashMap<>();

  /**
   * Retrieves the Claim Store item by id.
   *
   * @param claimId Identifier of the item to be retrieved
   * @return Claim Store item
   */
  public ClaimStoreItem get(Long claimId) {
    String key = String.valueOf(claimId);
    return store.get(key);
  }

  /**
   * Puts a new item in Claim Store.
   *
   * @param item New item to be stored in Claim Store
   */
  public void put(ClaimStoreItem item) {
    ClaimDetail detail = item.getClaimDetail();
    Long id = detail.getClaimId();
    String key = String.valueOf(id);
    store.put(key, item);
  }
}
