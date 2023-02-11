package gov.va.vro.mockbipclaims.configuration;

import gov.va.vro.mockbipclaims.model.ClaimDetail;
import gov.va.vro.mockbipclaims.model.ContentionSummary;
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

  public void backupAllCanChange() {
    originalLifecycleStatus = claimDetail.getClaimLifecycleStatus();
    originalContentions = new ArrayList<>();
    for (ContentionSummary contention : contentions) {
      originalContentions.add(contention);
    }
  }

  public void reset() {
    claimDetail.setClaimLifecycleStatus(originalLifecycleStatus);
    contentions = new ArrayList<>();
    for (ContentionSummary contention : originalContentions) {
      contentions.add(contention);
    }
  }
}
