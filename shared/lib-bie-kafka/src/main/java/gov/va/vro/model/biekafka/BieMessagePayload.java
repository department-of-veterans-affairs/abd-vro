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
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_ASSOCIATED_TO_CLAIM",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_CLASSIFIED",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED"
  })
  private String benefitClaimTypeCode;

  @TargetEvents({
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_ASSOCIATED_TO_CLAIM",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_CLASSIFIED",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED"
  })
  private String actorStation;

  @TargetEvents({
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_ASSOCIATED_TO_CLAIM",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_CLASSIFIED",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED"
  })
  private String details;

  @TargetEvents({
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_ASSOCIATED_TO_CLAIM",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_CLASSIFIED",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED"
  })
  private Long veteranParticipantId;

  @TargetEvents({
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_ASSOCIATED_TO_CLAIM",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_CLASSIFIED",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED"
  })
  private String contentionClassificationName;

  @TargetEvents({
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_ASSOCIATED_TO_CLAIM",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_CLASSIFIED",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED"
  })
  private String diagnosticTypeCode;

  @TargetEvents({"BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED"})
  private String journalStatusTypeCode;

  @TargetEvents({
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_ASSOCIATED_TO_CLAIM",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_CLASSIFIED",
    "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED"
  })
  private Long dateAdded;

  @TargetEvents({"BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED"})
  private Long dateCompleted;

  @TargetEvents({"BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED"})
  private Long dateUpdated;
}
