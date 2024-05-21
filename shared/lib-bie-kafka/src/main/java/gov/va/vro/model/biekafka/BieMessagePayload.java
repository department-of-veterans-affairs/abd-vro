package gov.va.vro.model.biekafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import gov.va.vro.model.biekafka.annotation.Ignore;
import gov.va.vro.model.biekafka.annotation.NoLogging;
import gov.va.vro.model.biekafka.annotation.TargetEvents;
import lombok.*;

import java.lang.reflect.Field;

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
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_CLASSIFIED_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_UPDATED_V02"
  })
  private String benefitClaimTypeCode;

  @NoLogging
  @TargetEvents({
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_DELETED_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_UPDATED_V02"
  })
  private String description;

  @TargetEvents({
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_CLASSIFIED_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_UPDATED_V02"
  })
  private String actorStation;

  @TargetEvents({
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_COMPLETED_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_CLASSIFIED_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_UPDATED_V02"
  })
  private String actorUserId;

  @TargetEvents({
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_CLASSIFIED_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_UPDATED_V02"
  })
  private String details;

  @TargetEvents({
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_CLASSIFIED_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_UPDATED_V02"
  })
  private Long veteranParticipantId;

  @TargetEvents({
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_CLASSIFIED_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_UPDATED_V02"
  })
  private String contentionClassificationName;

  @TargetEvents({
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_CLASSIFIED_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_UPDATED_V02"
  })
  private String diagnosticTypeCode;

  @TargetEvents({"BIA_SERVICES_BIE_CATALOG_CONTENTION_UPDATED_V02"})
  private String journalStatusTypeCode;

  @TargetEvents({
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_CLASSIFIED_V02",
    "BIA_SERVICES_BIE_CATALOG_CONTENTION_UPDATED_V02"
  })
  private Long dateAdded;

  @TargetEvents({"BIA_SERVICES_BIE_CATALOG_CONTENTION_UPDATED_V02"})
  private Long dateCompleted;

  @TargetEvents({"BIA_SERVICES_BIE_CATALOG_CONTENTION_UPDATED_V02"})
  private Long dateUpdated;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getSimpleName()).append(" {");
    Field[] fields = this.getClass().getDeclaredFields();

    boolean first = true;
    for (Field field : fields) {
      field.setAccessible(true);
      if (!field.isAnnotationPresent(NoLogging.class)) {
        if (!first) {
          sb.append(", ");
        }
        first = false;
        try {
          sb.append(field.getName()).append(": ").append(field.get(this));
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }

    sb.append("}");
    return sb.toString();
  }
}
