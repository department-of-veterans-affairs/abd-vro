package gov.va.vro.mockbipclaims.model.store;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
public class ModifyingActionStore {
  private Set<Long> lifecycleStatusUpdates = new HashSet<>();
  private Set<Long> contentionsUpdates = new HashSet<>();

  /**
   * Delete all updates for the claim.
   *
   * @param claimId Claim id
   */
  public void reset(Long claimId) {
    lifecycleStatusUpdates.remove(claimId);
    contentionsUpdates.remove(claimId);
  }

  /**
   * Add the claim to lifecycle status updated list.
   *
   * @param claimId Claim id
   */
  public void addLifecycleStatusUpdate(Long claimId) {
    lifecycleStatusUpdates.add(claimId);
  }

  /**
   * Add the claim to contentions updated list.
   *
   * @param claimId Claim id
   */
  public void addContentionsUpdate(Long claimId) {
    contentionsUpdates.add(claimId);
  }

  /**
   * Retrieve if the claim is in lifecycle status updated list.
   *
   * @param claimId Claim id
   */
  public boolean isLifecycleStatusUpdated(Long claimId) {
    return lifecycleStatusUpdates.contains(claimId);
  }

  /**
   * Retrieve if the claim is in contentions updated list.
   *
   * @param claimId Claim id
   */
  public boolean isContentionsUpdated(Long claimId) {
    return contentionsUpdates.contains(claimId);
  }
}
