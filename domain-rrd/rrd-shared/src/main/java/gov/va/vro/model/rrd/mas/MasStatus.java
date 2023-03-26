package gov.va.vro.model.rrd.mas;

import java.util.HashMap;
import java.util.Map;

/**
 * MAS status class.
 *
 * @author warren @Date 10/12/22
 */
public enum MasStatus {
  INPROGRESS("inProgress"),
  PROCESSED("processed"),
  OFFRAMPED("offramped"),
  VRONOTIFIED("vroNotified"),
  UNKNOWN("");

  private final String status;

  private static final Map<String, MasStatus> lookup = new HashMap<String, MasStatus>();

  static {
    for (MasStatus d : MasStatus.values()) {
      lookup.put(d.getStatus(), d);
    }
  }

  public static MasStatus getMasStatus(String status) {
    return lookup.get(status) == null ? UNKNOWN : lookup.get(status);
  }

  MasStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  /**
   * Gets MAS status.
   *
   * @param status status
   * @return return.
   */
  public static MasStatus getMasStatus1(String status) {
    for (MasStatus s : MasStatus.values()) {
      if (status.equals(s.getStatus())) {
        return s;
      }
    }
    return UNKNOWN;
  }
}
