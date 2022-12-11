package gov.va.vro.model.bip;

/** @author warren @Date 12/9/22 */
public enum CLAIM_STATUS {
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

  CLAIM_STATUS(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public static CLAIM_STATUS getStatusCode(String desc) {
    for (CLAIM_STATUS s : values()) {
      if (s.getDescription().equals(desc)) {
        return s;
      }
    }
    return null;
  }
}
