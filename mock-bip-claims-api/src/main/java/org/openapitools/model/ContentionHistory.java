package org.openapitools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** The list of contentions. */
@Schema(name = "contention_history", description = "The list of contentions")
@JsonTypeName("contention_history")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ContentionHistory {

  @JsonProperty("action")
  private String action;

  @JsonProperty("actionResult")
  private String actionResult;

  @JsonProperty("actorUserId")
  private String actorUserId;

  @JsonProperty("actorApplicationId")
  private String actorApplicationId;

  @JsonProperty("actorStationId")
  private String actorStationId;

  @JsonProperty("automationIndicator")
  private Boolean automationIndicator;

  @JsonProperty("benefitClaimTypeCode")
  private String benefitClaimTypeCode;

  @JsonProperty("claimId")
  private Long claimId;

  @JsonProperty("contentionClassificationName")
  private String contentionClassificationName;

  @JsonProperty("contentionId")
  private Long contentionId;

  @JsonProperty("contentionStatusTypeCode")
  private String contentionStatusTypeCode;

  @JsonProperty("contentionTypeCode")
  private String contentionTypeCode;

  @JsonProperty("lifecycleStatus")
  private String lifecycleStatus;

  @JsonProperty("details")
  private String details;

  @JsonProperty("diagnosticTypeCode")
  private String diagnosticTypeCode;

  @JsonProperty("eventTime")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime eventTime;

  @JsonProperty("triggeringEventTime")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime triggeringEventTime;

  @JsonProperty("completedDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime completedDate;

  @JsonProperty("claimantText")
  private String claimantText;

  @JsonProperty("source")
  private String source;

  @JsonProperty("lifecycleStatusChangeRequested")
  private Boolean lifecycleStatusChangeRequested;

  public ContentionHistory action(String action) {
    this.action = action;
    return this;
  }

  /**
   * The action this history item captures.
   *
   * @return action
   */
  @Schema(
      name = "action",
      example = "Updated Status Contention",
      description = "The action this history item captures.")
  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public ContentionHistory actionResult(String actionResult) {
    this.actionResult = actionResult;
    return this;
  }

  /**
   * Whether or not the action was completed successfully.
   *
   * @return actionResult
   */
  @Schema(
      name = "actionResult",
      example = "Successful",
      description = "Whether or not the action was completed successfully.")
  public String getActionResult() {
    return actionResult;
  }

  public void setActionResult(String actionResult) {
    this.actionResult = actionResult;
  }

  public ContentionHistory actorUserId(String actorUserId) {
    this.actorUserId = actorUserId;
    return this;
  }

  /**
   * The user that took this action against the contention.
   *
   * @return actorUserId
   */
  @Schema(
      name = "actorUserId",
      example = "BIPCLAIMSYSACCT",
      description = "The user that took this action against the contention.")
  public String getActorUserId() {
    return actorUserId;
  }

  public void setActorUserId(String actorUserId) {
    this.actorUserId = actorUserId;
  }

  public ContentionHistory actorApplicationId(String actorApplicationId) {
    this.actorApplicationId = actorApplicationId;
    return this;
  }

  /**
   * The application ID from which the user took this action against the contention.
   *
   * @return actorApplicationId
   */
  @Schema(
      name = "actorApplicationId",
      example = "BIPCLAIMSAPI",
      description =
          "The application ID from which the user took this action against the contention.")
  public String getActorApplicationId() {
    return actorApplicationId;
  }

  public void setActorApplicationId(String actorApplicationId) {
    this.actorApplicationId = actorApplicationId;
  }

  public ContentionHistory actorStationId(String actorStationId) {
    this.actorStationId = actorStationId;
    return this;
  }

  /**
   * The station ID of the user that took this action against the contention.
   *
   * @return actorStationId
   */
  @Schema(
      name = "actorStationId",
      example = "281",
      description = "The station ID of the user that took this action against the contention.")
  public String getActorStationId() {
    return actorStationId;
  }

  public void setActorStationId(String actorStationId) {
    this.actorStationId = actorStationId;
  }

  public ContentionHistory automationIndicator(Boolean automationIndicator) {
    this.automationIndicator = automationIndicator;
    return this;
  }

  /**
   * Indicator if the contention has been automated.
   *
   * @return automationIndicator
   */
  @Schema(
      name = "automationIndicator",
      example = "true",
      description = "Indicator if the contention has been automated.")
  public Boolean getAutomationIndicator() {
    return automationIndicator;
  }

  public void setAutomationIndicator(Boolean automationIndicator) {
    this.automationIndicator = automationIndicator;
  }

  public ContentionHistory benefitClaimTypeCode(String benefitClaimTypeCode) {
    this.benefitClaimTypeCode = benefitClaimTypeCode;
    return this;
  }

  /**
   * The benefit claim type code.
   *
   * @return benefitClaimTypeCode
   */
  @Schema(
      name = "benefitClaimTypeCode",
      example = "160SCBPMC",
      description = "The benefit claim type code.")
  public String getBenefitClaimTypeCode() {
    return benefitClaimTypeCode;
  }

  public void setBenefitClaimTypeCode(String benefitClaimTypeCode) {
    this.benefitClaimTypeCode = benefitClaimTypeCode;
  }

  public ContentionHistory claimId(Long claimId) {
    this.claimId = claimId;
    return this;
  }

  /**
   * The CorpDB BNFT_CLAIM_ID.
   *
   * @return claimId
   */
  @Schema(name = "claimId", example = "9666422", description = "The CorpDB BNFT_CLAIM_ID.")
  public Long getClaimId() {
    return claimId;
  }

  public void setClaimId(Long claimId) {
    this.claimId = claimId;
  }

  public ContentionHistory contentionClassificationName(String contentionClassificationName) {
    this.contentionClassificationName = contentionClassificationName;
    return this;
  }

  /**
   * A detailed description of the classification term as defined by the VBA medical specialists.
   *
   * @return contentionClassificationName
   */
  @Schema(
      name = "contentionClassificationName",
      example = "NEW",
      description =
          """
          A detailed description of the classification term as defined by the VBA medical
          specialists.
          """)
  public String getContentionClassificationName() {
    return contentionClassificationName;
  }

  public void setContentionClassificationName(String contentionClassificationName) {
    this.contentionClassificationName = contentionClassificationName;
  }

  public ContentionHistory contentionId(Long contentionId) {
    this.contentionId = contentionId;
    return this;
  }

  /**
   * The contention ID from CorpDB the event or action is taken on.
   *
   * @return contentionId
   */
  @Schema(
      name = "contentionId",
      example = "71773",
      description = "The contention ID from CorpDB the event or action is taken on.")
  public Long getContentionId() {
    return contentionId;
  }

  public void setContentionId(Long contentionId) {
    this.contentionId = contentionId;
  }

  public ContentionHistory contentionStatusTypeCode(String contentionStatusTypeCode) {
    this.contentionStatusTypeCode = contentionStatusTypeCode;
    return this;
  }

  /**
   * Status of a contention as recorded in CorpDB. The contention’s lifecycle status provides more
   * detail.
   *
   * @return contentionStatusTypeCode
   */
  @Schema(
      name = "contentionStatusTypeCode",
      example = "C",
      description =
          """
          Status of a contention as recorded in CorpDB. The contention’s lifecycle status
          provides more detail.
          """)
  public String getContentionStatusTypeCode() {
    return contentionStatusTypeCode;
  }

  public void setContentionStatusTypeCode(String contentionStatusTypeCode) {
    this.contentionStatusTypeCode = contentionStatusTypeCode;
  }

  public ContentionHistory contentionTypeCode(String contentionTypeCode) {
    this.contentionTypeCode = contentionTypeCode;
    return this;
  }

  /**
   * A code representing the type of contention.
   *
   * @return contentionTypeCode
   */
  @Schema(
      name = "contentionTypeCode",
      example = "NEW",
      description = "A code representing the type of contention.")
  public String getContentionTypeCode() {
    return contentionTypeCode;
  }

  public void setContentionTypeCode(String contentionTypeCode) {
    this.contentionTypeCode = contentionTypeCode;
  }

  public ContentionHistory lifecycleStatus(String lifecycleStatus) {
    this.lifecycleStatus = lifecycleStatus;
    return this;
  }

  /**
   * The lifecycle status of the contention captured at the time this action was taken.
   *
   * @return lifecycleStatus
   */
  @Schema(
      name = "lifecycleStatus",
      example = "Closed",
      description =
          "The lifecycle status of the contention captured at the time this action was taken.")
  public String getLifecycleStatus() {
    return lifecycleStatus;
  }

  public void setLifecycleStatus(String lifecycleStatus) {
    this.lifecycleStatus = lifecycleStatus;
  }

  public ContentionHistory details(String details) {
    this.details = details;
    return this;
  }

  /**
   * User provided description of this contention event.
   *
   * @return details
   */
  @Schema(
      name = "details",
      example = "Automation completed",
      description = "User provided description of this contention event.")
  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public ContentionHistory diagnosticTypeCode(String diagnosticTypeCode) {
    this.diagnosticTypeCode = diagnosticTypeCode;
    return this;
  }

  /**
   * Represents the proper medical diagnosis that has been assigned to previously rated contentions.
   *
   * @return diagnosticTypeCode
   */
  @Schema(
      name = "diagnosticTypeCode",
      example = "9411",
      description =
          """
          Represents the proper medical diagnosis that has been assigned to previously
          rated contentions.
          """)
  public String getDiagnosticTypeCode() {
    return diagnosticTypeCode;
  }

  public void setDiagnosticTypeCode(String diagnosticTypeCode) {
    this.diagnosticTypeCode = diagnosticTypeCode;
  }

  public ContentionHistory eventTime(OffsetDateTime eventTime) {
    this.eventTime = eventTime;
    return this;
  }

  /**
   * The time the action against the contention occurred.
   *
   * @return eventTime
   */
  @Valid
  @Schema(name = "eventTime", description = "The time the action against the contention occurred.")
  public OffsetDateTime getEventTime() {
    return eventTime;
  }

  public void setEventTime(OffsetDateTime eventTime) {
    this.eventTime = eventTime;
  }

  public ContentionHistory triggeringEventTime(OffsetDateTime triggeringEventTime) {
    this.triggeringEventTime = triggeringEventTime;
    return this;
  }

  /**
   * The time of the event that triggered this action against the contention.
   *
   * @return triggeringEventTime
   */
  @Valid
  @Schema(
      name = "triggeringEventTime",
      description = "The time of the event that triggered this action against the contention.")
  public OffsetDateTime getTriggeringEventTime() {
    return triggeringEventTime;
  }

  public void setTriggeringEventTime(OffsetDateTime triggeringEventTime) {
    this.triggeringEventTime = triggeringEventTime;
  }

  public ContentionHistory completedDate(OffsetDateTime completedDate) {
    this.completedDate = completedDate;
    return this;
  }

  /**
   * Represents the date in which the contention was promulgated.
   *
   * @return completedDate
   */
  @Valid
  @Schema(
      name = "completedDate",
      description = "Represents the date in which the contention was promulgated.")
  public OffsetDateTime getCompletedDate() {
    return completedDate;
  }

  public void setCompletedDate(OffsetDateTime completedDate) {
    this.completedDate = completedDate;
  }

  public ContentionHistory claimantText(String claimantText) {
    this.claimantText = claimantText;
    return this;
  }

  /**
   * A description of the contention in the words of the claimant.
   *
   * @return claimantText
   */
  @Schema(
      name = "claimantText",
      example = "PTSD",
      description = "A description of the contention in the words of the claimant.")
  public String getClaimantText() {
    return claimantText;
  }

  public void setClaimantText(String claimantText) {
    this.claimantText = claimantText;
  }

  public ContentionHistory source(String source) {
    this.source = source;
    return this;
  }

  /**
   * Source of a contention history or application that caused the change.
   *
   * @return source
   */
  @Schema(
      name = "source",
      example = "CLAIMS_API",
      description = "Source of a contention history or application that caused the change")
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public ContentionHistory lifecycleStatusChangeRequested(Boolean lifecycleStatusChangeRequested) {
    this.lifecycleStatusChangeRequested = lifecycleStatusChangeRequested;
    return this;
  }

  /**
   * Whether or not the contention's lifecycle status was explicitly set during this event.
   *
   * @return lifecycleStatusChangeRequested
   */
  @Schema(
      name = "lifecycleStatusChangeRequested",
      example = "true",
      description =
          "Whether or not the contention's lifecycle status was explicitly set during this event.")
  public Boolean getLifecycleStatusChangeRequested() {
    return lifecycleStatusChangeRequested;
  }

  public void setLifecycleStatusChangeRequested(Boolean lifecycleStatusChangeRequested) {
    this.lifecycleStatusChangeRequested = lifecycleStatusChangeRequested;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentionHistory contentionHistory = (ContentionHistory) o;
    return Objects.equals(this.action, contentionHistory.action)
        && Objects.equals(this.actionResult, contentionHistory.actionResult)
        && Objects.equals(this.actorUserId, contentionHistory.actorUserId)
        && Objects.equals(this.actorApplicationId, contentionHistory.actorApplicationId)
        && Objects.equals(this.actorStationId, contentionHistory.actorStationId)
        && Objects.equals(this.automationIndicator, contentionHistory.automationIndicator)
        && Objects.equals(this.benefitClaimTypeCode, contentionHistory.benefitClaimTypeCode)
        && Objects.equals(this.claimId, contentionHistory.claimId)
        && Objects.equals(
            this.contentionClassificationName, contentionHistory.contentionClassificationName)
        && Objects.equals(this.contentionId, contentionHistory.contentionId)
        && Objects.equals(this.contentionStatusTypeCode, contentionHistory.contentionStatusTypeCode)
        && Objects.equals(this.contentionTypeCode, contentionHistory.contentionTypeCode)
        && Objects.equals(this.lifecycleStatus, contentionHistory.lifecycleStatus)
        && Objects.equals(this.details, contentionHistory.details)
        && Objects.equals(this.diagnosticTypeCode, contentionHistory.diagnosticTypeCode)
        && Objects.equals(this.eventTime, contentionHistory.eventTime)
        && Objects.equals(this.triggeringEventTime, contentionHistory.triggeringEventTime)
        && Objects.equals(this.completedDate, contentionHistory.completedDate)
        && Objects.equals(this.claimantText, contentionHistory.claimantText)
        && Objects.equals(this.source, contentionHistory.source)
        && Objects.equals(
            this.lifecycleStatusChangeRequested, contentionHistory.lifecycleStatusChangeRequested);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        action,
        actionResult,
        actorUserId,
        actorApplicationId,
        actorStationId,
        automationIndicator,
        benefitClaimTypeCode,
        claimId,
        contentionClassificationName,
        contentionId,
        contentionStatusTypeCode,
        contentionTypeCode,
        lifecycleStatus,
        details,
        diagnosticTypeCode,
        eventTime,
        triggeringEventTime,
        completedDate,
        claimantText,
        source,
        lifecycleStatusChangeRequested);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentionHistory {\n");
    sb.append("    action: ").append(toIndentedString(action)).append("\n");
    sb.append("    actionResult: ").append(toIndentedString(actionResult)).append("\n");
    sb.append("    actorUserId: ").append(toIndentedString(actorUserId)).append("\n");
    sb.append("    actorApplicationId: ").append(toIndentedString(actorApplicationId)).append("\n");
    sb.append("    actorStationId: ").append(toIndentedString(actorStationId)).append("\n");
    sb.append("    automationIndicator: ")
        .append(toIndentedString(automationIndicator))
        .append("\n");
    sb.append("    benefitClaimTypeCode: ")
        .append(toIndentedString(benefitClaimTypeCode))
        .append("\n");
    sb.append("    claimId: ").append(toIndentedString(claimId)).append("\n");
    sb.append("    contentionClassificationName: ")
        .append(toIndentedString(contentionClassificationName))
        .append("\n");
    sb.append("    contentionId: ").append(toIndentedString(contentionId)).append("\n");
    sb.append("    contentionStatusTypeCode: ")
        .append(toIndentedString(contentionStatusTypeCode))
        .append("\n");
    sb.append("    contentionTypeCode: ").append(toIndentedString(contentionTypeCode)).append("\n");
    sb.append("    lifecycleStatus: ").append(toIndentedString(lifecycleStatus)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
    sb.append("    diagnosticTypeCode: ").append(toIndentedString(diagnosticTypeCode)).append("\n");
    sb.append("    eventTime: ").append(toIndentedString(eventTime)).append("\n");
    sb.append("    triggeringEventTime: ")
        .append(toIndentedString(triggeringEventTime))
        .append("\n");
    sb.append("    completedDate: ").append(toIndentedString(completedDate)).append("\n");
    sb.append("    claimantText: ").append(toIndentedString(claimantText)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    lifecycleStatusChangeRequested: ")
        .append(toIndentedString(lifecycleStatusChangeRequested))
        .append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
