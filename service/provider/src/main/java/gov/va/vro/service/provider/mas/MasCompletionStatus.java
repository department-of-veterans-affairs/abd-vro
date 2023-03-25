package gov.va.vro.service.provider.mas;

import gov.va.vro.model.bip.ClaimStatus;
import gov.va.vro.service.provider.ClaimProps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum MasCompletionStatus {
  OFF_RAMP("off-ramp", false, true, ClaimStatus.OPEN),
  EXAM_ORDER("exam order", false, false, ClaimStatus.OPEN),
  READY_FOR_DECISION("ready for decision", true, false, ClaimStatus.RFD);

  private final String description;
  private final boolean automationIndicator;
  private final boolean removeRRDSpecialIssue;
  private final ClaimStatus claimStatus;

  public Set<String> getSpecialIssuesToRemove(ClaimProps claimProps) {
    Set<String> result = new HashSet<>();
    result.add(claimProps.getSpecialIssue1());
    if (removeRRDSpecialIssue) {
      result.add(claimProps.getSpecialIssue2());
    }
    return result;
  }

  public static MasCompletionStatus of(MasCamelStage origin, Boolean sufficientForFastTracking) {
    if (origin == MasCamelStage.START_COMPLETE || sufficientForFastTracking == null) {
      return OFF_RAMP;
    }
    return sufficientForFastTracking ? READY_FOR_DECISION : EXAM_ORDER;
  }
}
