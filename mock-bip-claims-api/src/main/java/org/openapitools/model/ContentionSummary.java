package org.openapitools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/** ContentionSummary. */
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ContentionSummary {

  @JsonProperty("medicalInd")
  private Boolean medicalInd;

  @JsonProperty("beginDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime beginDate;

  @JsonProperty("createDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createDate;

  @JsonProperty("altContentionName")
  private String altContentionName;

  @JsonProperty("completedDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime completedDate;

  @JsonProperty("notificationDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime notificationDate;

  @JsonProperty("contentionTypeCode")
  private String contentionTypeCode;

  @JsonProperty("classificationType")
  private Long classificationType;

  @JsonProperty("diagnosticTypeCode")
  private String diagnosticTypeCode;

  @JsonProperty("claimantText")
  private String claimantText;

  @JsonProperty("contentionStatusTypeCode")
  private String contentionStatusTypeCode;

  @JsonProperty("originalSourceTypeCode")
  private String originalSourceTypeCode;

  @JsonProperty("specialIssueCodes")
  @Valid
  private List<String> specialIssueCodes = null;

  @JsonProperty("associatedTrackedItems")
  @Valid
  private List<TrackedItemAssociationsOnContention> associatedTrackedItems = null;

  @JsonProperty("contentionId")
  private Long contentionId;

  @JsonProperty("lastModified")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime lastModified;

  @JsonProperty("lifecycleStatus")
  private String lifecycleStatus;

  @JsonProperty("action")
  private String action;

  @JsonProperty("automationIndicator")
  private Boolean automationIndicator;

  @JsonProperty("summaryDateTime")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime summaryDateTime;

  public ContentionSummary medicalInd(Boolean medicalInd) {
    this.medicalInd = medicalInd;
    return this;
  }

  /**
   * Get medicalInd.
   *
   * @return medicalInd
   */
  @NotNull
  @Schema(name = "medicalInd")
  public Boolean getMedicalInd() {
    return medicalInd;
  }

  public void setMedicalInd(Boolean medicalInd) {
    this.medicalInd = medicalInd;
  }

  public ContentionSummary beginDate(OffsetDateTime beginDate) {
    this.beginDate = beginDate;
    return this;
  }

  /**
   * Get beginDate.
   *
   * @return beginDate
   */
  @NotNull
  @Valid
  @Schema(name = "beginDate")
  public OffsetDateTime getBeginDate() {
    return beginDate;
  }

  public void setBeginDate(OffsetDateTime beginDate) {
    this.beginDate = beginDate;
  }

  public ContentionSummary createDate(OffsetDateTime createDate) {
    this.createDate = createDate;
    return this;
  }

  /**
   * Get createDate.
   *
   * @return createDate
   */
  @Valid
  @Schema(name = "createDate")
  public OffsetDateTime getCreateDate() {
    return createDate;
  }

  public void setCreateDate(OffsetDateTime createDate) {
    this.createDate = createDate;
  }

  public ContentionSummary altContentionName(String altContentionName) {
    this.altContentionName = altContentionName;
    return this;
  }

  /**
   * Get altContentionName.
   *
   * @return altContentionName
   */
  @Schema(name = "altContentionName", example = "Alternate Name")
  public String getAltContentionName() {
    return altContentionName;
  }

  public void setAltContentionName(String altContentionName) {
    this.altContentionName = altContentionName;
  }

  public ContentionSummary completedDate(OffsetDateTime completedDate) {
    this.completedDate = completedDate;
    return this;
  }

  /**
   * Get completedDate.
   *
   * @return completedDate
   */
  @Valid
  @Schema(name = "completedDate")
  public OffsetDateTime getCompletedDate() {
    return completedDate;
  }

  public void setCompletedDate(OffsetDateTime completedDate) {
    this.completedDate = completedDate;
  }

  public ContentionSummary notificationDate(OffsetDateTime notificationDate) {
    this.notificationDate = notificationDate;
    return this;
  }

  /**
   * Get notificationDate.
   *
   * @return notificationDate
   */
  @Valid
  @Schema(name = "notificationDate")
  public OffsetDateTime getNotificationDate() {
    return notificationDate;
  }

  public void setNotificationDate(OffsetDateTime notificationDate) {
    this.notificationDate = notificationDate;
  }

  public ContentionSummary contentionTypeCode(String contentionTypeCode) {
    this.contentionTypeCode = contentionTypeCode;
    return this;
  }

  /**
   * Get contentionTypeCode.
   *
   * @return contentionTypeCode
   */
  @NotNull
  @Schema(name = "contentionTypeCode", example = "NEW")
  public String getContentionTypeCode() {
    return contentionTypeCode;
  }

  public void setContentionTypeCode(String contentionTypeCode) {
    this.contentionTypeCode = contentionTypeCode;
  }

  public ContentionSummary classificationType(Long classificationType) {
    this.classificationType = classificationType;
    return this;
  }

  /**
   * Get classificationType.
   *
   * @return classificationType
   */
  @NotNull
  @Schema(name = "classificationType", example = "1250")
  public Long getClassificationType() {
    return classificationType;
  }

  public void setClassificationType(Long classificationType) {
    this.classificationType = classificationType;
  }

  public ContentionSummary diagnosticTypeCode(String diagnosticTypeCode) {
    this.diagnosticTypeCode = diagnosticTypeCode;
    return this;
  }

  /**
   * Get diagnosticTypeCode.
   *
   * @return diagnosticTypeCode
   */
  @Schema(name = "diagnosticTypeCode", example = "6100")
  public String getDiagnosticTypeCode() {
    return diagnosticTypeCode;
  }

  public void setDiagnosticTypeCode(String diagnosticTypeCode) {
    this.diagnosticTypeCode = diagnosticTypeCode;
  }

  public ContentionSummary claimantText(String claimantText) {
    this.claimantText = claimantText;
    return this;
  }

  /**
   * Get claimantText.
   *
   * @return claimantText
   */
  @NotNull
  @Schema(name = "claimantText", example = "tendinitis/bilateral")
  public String getClaimantText() {
    return claimantText;
  }

  public void setClaimantText(String claimantText) {
    this.claimantText = claimantText;
  }

  public ContentionSummary contentionStatusTypeCode(String contentionStatusTypeCode) {
    this.contentionStatusTypeCode = contentionStatusTypeCode;
    return this;
  }

  /**
   * Get contentionStatusTypeCode.
   *
   * @return contentionStatusTypeCode
   */
  @Schema(name = "contentionStatusTypeCode", example = "C")
  public String getContentionStatusTypeCode() {
    return contentionStatusTypeCode;
  }

  public void setContentionStatusTypeCode(String contentionStatusTypeCode) {
    this.contentionStatusTypeCode = contentionStatusTypeCode;
  }

  public ContentionSummary originalSourceTypeCode(String originalSourceTypeCode) {
    this.originalSourceTypeCode = originalSourceTypeCode;
    return this;
  }

  /**
   * Get originalSourceTypeCode.
   *
   * @return originalSourceTypeCode
   */
  @Schema(name = "originalSourceTypeCode", example = "PHYS")
  public String getOriginalSourceTypeCode() {
    return originalSourceTypeCode;
  }

  public void setOriginalSourceTypeCode(String originalSourceTypeCode) {
    this.originalSourceTypeCode = originalSourceTypeCode;
  }

  public ContentionSummary specialIssueCodes(List<String> specialIssueCodes) {
    this.specialIssueCodes = specialIssueCodes;
    return this;
  }

  /**
   * Adds special issue codes.
   *
   * @param specialIssueCodesItem special issue code.
   * @returnceontention ummaruy
   */
  public ContentionSummary addSpecialIssueCodesItem(String specialIssueCodesItem) {
    if (this.specialIssueCodes == null) {
      this.specialIssueCodes = new ArrayList<>();
    }
    this.specialIssueCodes.add(specialIssueCodesItem);
    return this;
  }

  /**
   * Get specialIssueCodes.
   *
   * @return specialIssueCodes
   */
  @Schema(name = "specialIssueCodes", example = "[\"38USC1151\",\"AOOV\",\"ELIGIBILITY\"]")
  public List<String> getSpecialIssueCodes() {
    return specialIssueCodes;
  }

  public void setSpecialIssueCodes(List<String> specialIssueCodes) {
    this.specialIssueCodes = specialIssueCodes;
  }

  public ContentionSummary associatedTrackedItems(
      List<TrackedItemAssociationsOnContention> associatedTrackedItems) {
    this.associatedTrackedItems = associatedTrackedItems;
    return this;
  }

  /**
   * Retireves contention summary.
   *
   * @param associatedTrackedItemsItem associated item
   * @return conetention summary
   */
  public ContentionSummary addAssociatedTrackedItemsItem(
      TrackedItemAssociationsOnContention associatedTrackedItemsItem) {
    if (this.associatedTrackedItems == null) {
      this.associatedTrackedItems = new ArrayList<>();
    }
    this.associatedTrackedItems.add(associatedTrackedItemsItem);
    return this;
  }

  /**
   * Associated tracked items IDs on each contention.
   *
   * @return associatedTrackedItems
   */
  @Valid
  @Schema(
      name = "associatedTrackedItems",
      description = "Associated tracked items IDs on each contention")
  public List<TrackedItemAssociationsOnContention> getAssociatedTrackedItems() {
    return associatedTrackedItems;
  }

  public void setAssociatedTrackedItems(
      List<TrackedItemAssociationsOnContention> associatedTrackedItems) {
    this.associatedTrackedItems = associatedTrackedItems;
  }

  public ContentionSummary contentionId(Long contentionId) {
    this.contentionId = contentionId;
    return this;
  }

  /**
   * Get contentionId.
   *
   * @return contentionId
   */
  @NotNull
  @Schema(name = "contentionId", example = "5261")
  public Long getContentionId() {
    return contentionId;
  }

  public void setContentionId(Long contentionId) {
    this.contentionId = contentionId;
  }

  public ContentionSummary lastModified(OffsetDateTime lastModified) {
    this.lastModified = lastModified;
    return this;
  }

  /**
   * Last modified time for the contention being updated. If this does not match, the update will
   * be. rejected.
   *
   * @return lastModified
   */
  @NotNull
  @Valid
  @Schema(
      name = "lastModified",
      description =
          """
          Last modified time for the contention being updated. If this does not match, the update
          will be rejected.
          """)
  public OffsetDateTime getLastModified() {
    return lastModified;
  }

  public void setLastModified(OffsetDateTime lastModified) {
    this.lastModified = lastModified;
  }

  public ContentionSummary lifecycleStatus(String lifecycleStatus) {
    this.lifecycleStatus = lifecycleStatus;
    return this;
  }

  /**
   * Current status of the contention.
   *
   * @return lifecycleStatus
   */
  @Schema(
      name = "lifecycleStatus",
      example = "Ready for Decision",
      description = "Current status of the contention")
  public String getLifecycleStatus() {
    return lifecycleStatus;
  }

  public void setLifecycleStatus(String lifecycleStatus) {
    this.lifecycleStatus = lifecycleStatus;
  }

  public ContentionSummary action(String action) {
    this.action = action;
    return this;
  }

  /**
   * Action taken on contention.
   *
   * @return action
   */
  @Schema(
      name = "action",
      example = "Updated Contention",
      description = "Action taken on contention.")
  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public ContentionSummary automationIndicator(Boolean automationIndicator) {
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
      description = "Indicator if the contention has been automated")
  public Boolean getAutomationIndicator() {
    return automationIndicator;
  }

  public void setAutomationIndicator(Boolean automationIndicator) {
    this.automationIndicator = automationIndicator;
  }

  public ContentionSummary summaryDateTime(OffsetDateTime summaryDateTime) {
    this.summaryDateTime = summaryDateTime;
    return this;
  }

  /**
   * Get summaryDateTime.
   *
   * @return summaryDateTime
   */
  @Valid
  @Schema(name = "summaryDateTime")
  public OffsetDateTime getSummaryDateTime() {
    return summaryDateTime;
  }

  public void setSummaryDateTime(OffsetDateTime summaryDateTime) {
    this.summaryDateTime = summaryDateTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentionSummary contentionSummary = (ContentionSummary) o;
    return Objects.equals(this.medicalInd, contentionSummary.medicalInd)
        && Objects.equals(this.beginDate, contentionSummary.beginDate)
        && Objects.equals(this.createDate, contentionSummary.createDate)
        && Objects.equals(this.altContentionName, contentionSummary.altContentionName)
        && Objects.equals(this.completedDate, contentionSummary.completedDate)
        && Objects.equals(this.notificationDate, contentionSummary.notificationDate)
        && Objects.equals(this.contentionTypeCode, contentionSummary.contentionTypeCode)
        && Objects.equals(this.classificationType, contentionSummary.classificationType)
        && Objects.equals(this.diagnosticTypeCode, contentionSummary.diagnosticTypeCode)
        && Objects.equals(this.claimantText, contentionSummary.claimantText)
        && Objects.equals(this.contentionStatusTypeCode, contentionSummary.contentionStatusTypeCode)
        && Objects.equals(this.originalSourceTypeCode, contentionSummary.originalSourceTypeCode)
        && Objects.equals(this.specialIssueCodes, contentionSummary.specialIssueCodes)
        && Objects.equals(this.associatedTrackedItems, contentionSummary.associatedTrackedItems)
        && Objects.equals(this.contentionId, contentionSummary.contentionId)
        && Objects.equals(this.lastModified, contentionSummary.lastModified)
        && Objects.equals(this.lifecycleStatus, contentionSummary.lifecycleStatus)
        && Objects.equals(this.action, contentionSummary.action)
        && Objects.equals(this.automationIndicator, contentionSummary.automationIndicator)
        && Objects.equals(this.summaryDateTime, contentionSummary.summaryDateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        medicalInd,
        beginDate,
        createDate,
        altContentionName,
        completedDate,
        notificationDate,
        contentionTypeCode,
        classificationType,
        diagnosticTypeCode,
        claimantText,
        contentionStatusTypeCode,
        originalSourceTypeCode,
        specialIssueCodes,
        associatedTrackedItems,
        contentionId,
        lastModified,
        lifecycleStatus,
        action,
        automationIndicator,
        summaryDateTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentionSummary {\n");
    sb.append("    medicalInd: ").append(toIndentedString(medicalInd)).append("\n");
    sb.append("    beginDate: ").append(toIndentedString(beginDate)).append("\n");
    sb.append("    createDate: ").append(toIndentedString(createDate)).append("\n");
    sb.append("    altContentionName: ").append(toIndentedString(altContentionName)).append("\n");
    sb.append("    completedDate: ").append(toIndentedString(completedDate)).append("\n");
    sb.append("    notificationDate: ").append(toIndentedString(notificationDate)).append("\n");
    sb.append("    contentionTypeCode: ").append(toIndentedString(contentionTypeCode)).append("\n");
    sb.append("    classificationType: ").append(toIndentedString(classificationType)).append("\n");
    sb.append("    diagnosticTypeCode: ").append(toIndentedString(diagnosticTypeCode)).append("\n");
    sb.append("    claimantText: ").append(toIndentedString(claimantText)).append("\n");
    sb.append("    contentionStatusTypeCode: ")
        .append(toIndentedString(contentionStatusTypeCode))
        .append("\n");
    sb.append("    originalSourceTypeCode: ")
        .append(toIndentedString(originalSourceTypeCode))
        .append("\n");
    sb.append("    specialIssueCodes: ").append(toIndentedString(specialIssueCodes)).append("\n");
    sb.append("    associatedTrackedItems: ")
        .append(toIndentedString(associatedTrackedItems))
        .append("\n");
    sb.append("    contentionId: ").append(toIndentedString(contentionId)).append("\n");
    sb.append("    lastModified: ").append(toIndentedString(lastModified)).append("\n");
    sb.append("    lifecycleStatus: ").append(toIndentedString(lifecycleStatus)).append("\n");
    sb.append("    action: ").append(toIndentedString(action)).append("\n");
    sb.append("    automationIndicator: ")
        .append(toIndentedString(automationIndicator))
        .append("\n");
    sb.append("    summaryDateTime: ").append(toIndentedString(summaryDateTime)).append("\n");
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
