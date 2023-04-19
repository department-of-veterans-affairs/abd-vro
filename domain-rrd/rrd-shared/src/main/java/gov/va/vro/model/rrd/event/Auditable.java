package gov.va.vro.model.rrd.event;

import java.util.Map;

/** POJOs implementing this interface participate in audit events. */
public interface Auditable {

  String getEventId();

  Map<String, String> getDetails();

  String getDisplayName();
}
