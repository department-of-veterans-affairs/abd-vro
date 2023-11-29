package gov.va.vro.model.biekafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Setter
@Getter
@Builder(toBuilder = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class BieMessagePayload {
  // these are vro fields
  private Integer status;
  private String statusMessage;
  private ContentionEvent eventType;
  private Long notifiedAt;

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
  private String clmntTxt;
  private String journalStatusTypeCode;
  private Long dateAdded;
  private Long dateCompleted;
  private Long dateUpdated;
  private String eventDetails;
}
