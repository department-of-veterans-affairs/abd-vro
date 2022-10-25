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
  @Builder.Default private ZonedDateTime eventTime = ZonedDateTime.now();
}
