package gov.va.vro.mockbipclaims.model.store;

import gov.va.vro.mockbipclaims.model.bip.ClaimDetail;
import gov.va.vro.mockbipclaims.model.bip.ContentionSummary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ClaimStoreItem {
  String description;
  ClaimDetail claimDetail;
  List<ContentionSummary> contentions;

  public String originalLifecycleStatus;

  List<ContentionSummary> originalContentions;

  /** Saves the content that can change for later recovery. */
  public void backupAllCanChange() {
    originalLifecycleStatus = claimDetail.getClaimLifecycleStatus();
    originalContentions = new ArrayList<>();
    for (ContentionSummary contention : contentions) {
      originalContentions.add(contention);
    }
  }

  /** Restores original content of the data from back-ups. */
  public void reset() {
    claimDetail.setClaimLifecycleStatus(originalLifecycleStatus);
    contentions = new ArrayList<>();
    for (ContentionSummary contention : originalContentions) {
      contentions.add(contention);
    }
  }
}
