package gov.va.vro.mockbipclaims.model.bip;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

/** The list of contentions. */
@Schema(name = "contention_history", description = "The list of contentions")
@JsonTypeName("contention_history")
@Data
public class ContentionHistory {
  private String action;

  private String actionResult;

  private String actorUserId;

  private String actorApplicationId;

  private String actorStationId;

  private Boolean automationIndicator;

  private String benefitClaimTypeCode;

  private Long claimId;

  private String contentionClassificationName;

  private Long contentionId;

  private String contentionStatusTypeCode;

  private String contentionTypeCode;

  private String lifecycleStatus;

  private String details;

  private String diagnosticTypeCode;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime eventTime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime triggeringEventTime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime completedDate;

  private String claimantText;

  private String source;

  private Boolean lifecycleStatusChangeRequested;
}
