package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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

  @Type(type = "json")
  @Column(columnDefinition = "jsonb")
  private Map<String, String> details;

  @NotNull private ZonedDateTime eventTime = ZonedDateTime.now();
}
