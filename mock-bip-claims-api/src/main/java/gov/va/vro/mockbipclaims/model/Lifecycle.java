package gov.va.vro.mockbipclaims.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

/** Status updates for the lifecycle of a claim. */
@Schema(name = "Lifecycle", description = "Status updates for the lifecycle of a claim")
@Data
public class Lifecycle {
  private String lifecycleStatusTypeCode;

  private String lifecycleStatusTypeName;

  private String lifecycleStatusReasonTypeCode;

  private String lifecycleStatusReasonTypeName;

  private String reasonDetailTypeCode;

  private String reasonDetailTypeName;

  private String reasonText;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime changedDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime closedDate;
}
