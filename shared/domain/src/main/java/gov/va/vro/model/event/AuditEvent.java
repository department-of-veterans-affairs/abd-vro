package gov.va.vro.model.event;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Builder(toBuilder = true)
@ToString
public class AuditEvent {

  private String eventId;
  private String routeId;
  private Class<?> payloadType;
  private Throwable throwable;
  private String message;
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
        .throwable(exception)
        .build();
  }
}
