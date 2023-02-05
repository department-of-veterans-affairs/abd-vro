package org.openapitools.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/** The PhaseType is the status value that used to populate the EVSS ClaimStatus. */
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
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

  /**
   * Create from value.
   *
   * @param value input
   * @return Phase type
   */
  @JsonCreator
  public static PhaseType fromValue(String value) {
    for (PhaseType b : PhaseType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
