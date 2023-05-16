package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.type.SqlTypes;

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

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Map<String, String> details;

  @NotNull private ZonedDateTime eventTime = ZonedDateTime.now();
}
