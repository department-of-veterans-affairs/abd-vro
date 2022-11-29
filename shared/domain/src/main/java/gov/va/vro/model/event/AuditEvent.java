package gov.va.vro.model.event;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder(toBuilder = true)
public class AuditEvent {

  private String eventId;
  private String routeId;
  private Class<?> payloadType;
  private Throwable throwable;

  // WARNING: DO NOT STORE PII/PHI
  private String message;

  // WARNING: DO NOT STORE PII/PHI
  private String details;

  @Builder.Default private ZonedDateTime eventTime = ZonedDateTime.now();

  public boolean isException() {
    return throwable != null;
  }

  public static AuditEvent fromAuditable(Auditable body, String routeId, String message) {
    return AuditEvent.builder()
        .eventId(body.getEventId())
        .routeId(routeId)
        .payloadType(body.getClass())
        .message(message)
        .build();
  }

  public static AuditEvent fromException(Auditable body, String routeId, Throwable exception) {
    return AuditEvent.builder()
        .eventId(body.getEventId())
        .routeId(routeId)
        .payloadType(body.getClass())
        .message(exception.getMessage())
        .throwable(exception)
        .message(exception.getMessage())
        .build();
  }

  @Override
  public String toString() {
    if (isException()) {
      return String.format(
          "Exception occurred on route %s for %s(id = %s): %s\n"
              + "Please check the audit store for more information.",
          routeId, payloadType.getSimpleName(), eventId, message);
    } else {
      return toSimpleString();
    }
  }

  public String toSimpleString() {
    return "AuditEvent{"
        + "routeId='"
        + routeId
        + '\''
        + ", payloadType="
        + payloadType
        + ", message='"
        + message
        + '}';
  }
}
