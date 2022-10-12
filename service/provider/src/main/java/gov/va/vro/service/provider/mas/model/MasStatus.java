package gov.va.vro.service.provider.mas.model;

/** @author warren @Date 10/12/22 */
public enum MasStatus {
  INPROGRESS("inProgress"),
  PROCESSED("processed"),
  OFFRAMPED("offramped"),
  VRONOTIFIED("vroNotified"),
  UNKNOWN("");

  private final String status;

  MasStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public MasStatus getMasStatus(String status) {
    for (MasStatus s : MasStatus.values()) {
      if (status.equals(s.getStatus())) {
        return s;
      }
    }
    return UNKNOWN;
  }
}
