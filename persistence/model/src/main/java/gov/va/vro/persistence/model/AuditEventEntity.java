package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "audit_event")
public class AuditEventEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  private String eventId;
  private String routeId;
  private Class<?> payloadType;
  private Throwable exception;
  private String message;
  private ZonedDateTime eventTime = ZonedDateTime.now();
}
