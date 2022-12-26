package gov.va.vro.model.event;

/** POJOs implementing this interface participate in audit events. */
public interface Auditable {

  String getEventId();

  String getDetails();
}
