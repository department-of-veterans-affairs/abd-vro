package gov.va.vro.model.rrd.bip;

/**
 * Claim Status.
 *
 * @author warren @Date 12/9/22
 */
public enum ClaimStatus {
  RFD("Ready for Decision"),
  OPEN("Open"),
  SRFD("Secondary Ready for Decision"),
  RC("Rating Correction"),
  RI("Rating Incomplete"),
  RDC("Rating Decision Complete"),
  RW("Ready to Work"),
  RETOTH("Returned by Other User"),
  SELFRET("Self Returned"),
  PENDAUTH("Pending Authorization"),
  AUTH("Authorized"),
  CAN("Cancelled"),
  CLOSED("Closed"),
  DELETED("Deleted");

  private String description;

  ClaimStatus(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Get status code.
   *
   * @param desc desc.
   * @return return.
   */
  public static ClaimStatus getStatusCode(String desc) {
    for (ClaimStatus s : values()) {
      if (s.getDescription().equals(desc)) {
        return s;
      }
    }
    return null;
  }
}
