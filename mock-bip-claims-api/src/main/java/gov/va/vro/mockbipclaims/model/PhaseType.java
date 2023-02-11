package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonValue;

/** The PhaseType is the status value that used to populate the EVSS ClaimStatus. */
public enum PhaseType {
  CLAIM_RECEIVED("Claim Received"),

  UNDER_REVIEW("Under Review"),

  GATHERING_OF_EVIDENCE("Gathering of Evidence"),

  REVIEW_OF_EVIDENCE("Review of Evidence"),

  PREPARATION_FOR_DECISION("Preparation for Decision"),

  PENDING_DECISION_APPROVAL("Pending Decision Approval"),

  PREPARATION_FOR_NOTIFICATION("Preparation for Notification"),

  COMPLETE("Complete");

  private String value;

  PhaseType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
