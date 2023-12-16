package gov.va.vro.model.biekafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.va.vro.model.biekafka.annotation.Ignore;
import gov.va.vro.model.biekafka.annotation.TargetEvents;
import lombok.*;

@Setter
@Getter
@Builder(toBuilder = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class BieMessagePayload {
  // these are vro fields
  @Ignore
  private Integer status;
  @Ignore
  private String statusMessage;
  @Ignore
  private ContentionEvent eventType;
  @Ignore
  private Long notifiedAt;
  @Ignore
  private String description;

  // populated from kafka topic payload
  private String benefitClaimTypeCode;
  private String actorStation;
  private String details;
  private Long veteranParticipantId;
  private Long claimId;
  private Long contentionId;
  private String contentionTypeCode;
  private String contentionClassificationName;
  private String diagnosticTypeCode;
  private String actionName;
  private String actionResultName;
  private Boolean automationIndicator;
  private String contentionStatusTypeCode;
  private String currentLifecycleStatus;
  private Long eventTime;

  @TargetEvents({"CONTENTION_BIE_CONTENTION_UPDATED_V02"})
  private String journalStatusTypeCode;
  private Long dateAdded;
  private Long dateCompleted;
  private Long dateUpdated;
}
