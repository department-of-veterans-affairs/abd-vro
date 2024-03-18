package gov.va.vro.model.biekafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class BieMessagePayload {
  // These are VRO fields
  @Ignore private Integer status;
  @Ignore private String statusMessage;
  @Ignore private ContentionEvent eventType;
  @Ignore private Long notifiedAt;
  @Ignore private String description;

  // Fields without the @TargetEvents annotation are included in all five Kafka topics
  private Long claimId;
  private Long contentionId;
  private String actionName;
  private String actionResultName;
  private Boolean automationIndicator;
  private String contentionTypeCode;
  private String contentionStatusTypeCode;
  private String currentLifecycleStatus;
  private Long eventTime;

  // populated from kafka topic payload
  @TargetEvents({
    "CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "CONTENTION_BIE_CONTENTION_CLASSIFIED_V02",
    "CONTENTION_BIE_CONTENTION_UPDATED_V02"
  })
  private String benefitClaimTypeCode;

  @TargetEvents({
    "CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "CONTENTION_BIE_CONTENTION_CLASSIFIED_V02",
    "CONTENTION_BIE_CONTENTION_UPDATED_V02"
  })
  private String actorStation;

  @TargetEvents({
    "CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "CONTENTION_BIE_CONTENTION_CLASSIFIED_V02",
    "CONTENTION_BIE_CONTENTION_UPDATED_V02"
  })
  private String details;

  @TargetEvents({
    "CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "CONTENTION_BIE_CONTENTION_CLASSIFIED_V02",
    "CONTENTION_BIE_CONTENTION_UPDATED_V02"
  })
  private Long veteranParticipantId;

  @TargetEvents({
    "CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "CONTENTION_BIE_CONTENTION_CLASSIFIED_V02",
    "CONTENTION_BIE_CONTENTION_UPDATED_V02"
  })
  private String contentionClassificationName;

  @TargetEvents({
    "CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "CONTENTION_BIE_CONTENTION_CLASSIFIED_V02",
    "CONTENTION_BIE_CONTENTION_UPDATED_V02"
  })
  private String diagnosticTypeCode;

  @TargetEvents({"CONTENTION_BIE_CONTENTION_UPDATED_V02"})
  private String journalStatusTypeCode;

  @TargetEvents({
    "CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "CONTENTION_BIE_CONTENTION_CLASSIFIED_V02",
    "CONTENTION_BIE_CONTENTION_UPDATED_V02"
  })
  private Long dateAdded;

  @TargetEvents({"CONTENTION_BIE_CONTENTION_UPDATED_V02"})
  private Long dateCompleted;

  @TargetEvents({"CONTENTION_BIE_CONTENTION_UPDATED_V02"})
  private Long dateUpdated;
}
