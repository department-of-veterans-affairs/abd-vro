package gov.va.vro.model.rrd.event;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@Builder(toBuilder = true)
public class AuditEvent {

  private String eventId;
  private String routeId;
  private String payloadType;
  private Throwable throwable;

  // WARNING: DO NOT STORE PII/PHI
  private String[] messages;

  // WARNING: DO NOT STORE PII/PHI
  private Map<String, String> details;

  @Builder.Default private ZonedDateTime eventTime = ZonedDateTime.now();

  public boolean isException() {
    return throwable != null;
  }

  /**
   * From auditable.
   *
   * @param auditable auditable.
   * @param routeId route ID.
   * @param message message.
   * @return return.
   */
  public static AuditEvent fromAuditable(Auditable auditable, String routeId, String message) {

    return AuditEvent.builder()
        .eventId(auditable.getEventId())
        .routeId(routeId)
        .payloadType(auditable.getDisplayName())
        .messages(new String[] {message})
        .details(auditable.getDetails())
        .build();
  }

  /**
   * From auditable.
   *
   * @param auditable auditable.
   * @param routeId route ID.
   * @param messages messages.
   * @return return.
   */
  public static AuditEvent fromAuditable(Auditable auditable, String routeId, String[] messages) {
    return AuditEvent.builder()
        .eventId(auditable.getEventId())
        .routeId(routeId)
        .payloadType(auditable.getDisplayName())
        .messages(messages)
        .details(auditable.getDetails())
        .build();
  }
  /**
   * From exception.
   *
   * @param auditable auditable.
   * @param routeId route ID.
   * @param exception exception.
   * @return return.
   */
  public static AuditEvent fromException(Auditable auditable, String routeId, Throwable exception) {

    return AuditEvent.builder()
        .eventId(auditable.getEventId())
        .routeId(routeId)
        .payloadType(auditable.getDisplayName())
        .messages(new String[] {exception.getMessage()})
        .throwable(exception)
        .build();
  }

  /**
   * To string.
   *
   * @return the string.
   */
  @Override
  public String toString() {
    if (isException()) {
      return String.format(
          "Exception occurred on route %s for %s(id = %s): %s.\n"
              + "Please check the audit store for more information.",
          routeId, payloadType, eventId, String.join(", ", messages));
    } else {
      return toSimpleString();
    }
  }

  /**
   * To simple string.
   *
   * @return return string.
   */
  public String toSimpleString() {
    return "AuditEvent{"
        + "routeId='"
        + routeId
        + '\''
        + ", payloadType="
        + payloadType
        + ", message='"
        + String.join(",", messages)
        + '\''
        + '}';
  }
}
