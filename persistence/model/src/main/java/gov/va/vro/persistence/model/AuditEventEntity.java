package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

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

  @NotNull private String eventId;
  private String routeId;
  @NotNull private String payloadType;
  private String throwable;
  private String message;
  private String details;
  @NotNull private ZonedDateTime eventTime = ZonedDateTime.now();
}
