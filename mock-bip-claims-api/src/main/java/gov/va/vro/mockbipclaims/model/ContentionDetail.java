package gov.va.vro.mockbipclaims.model;

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

/** An object to provide more detailed data for a specific contention. */
@Schema(
    name = "ContentionDetail",
    description = "An object to provide more detailed data for a specific contention.")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ContentionDetail {

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

  @JsonProperty("contentionHistory")
  @Valid
  private List<ContentionHistory> contentionHistory = null;

  public ContentionDetail medicalInd(Boolean medicalInd) {
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

  public ContentionDetail beginDate(OffsetDateTime beginDate) {
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

  public ContentionDetail createDate(OffsetDateTime createDate) {
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

  public ContentionDetail altContentionName(String altContentionName) {
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

  public ContentionDetail completedDate(OffsetDateTime completedDate) {
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

  public ContentionDetail notificationDate(OffsetDateTime notificationDate) {
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

  public ContentionDetail contentionTypeCode(String contentionTypeCode) {
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

  public ContentionDetail classificationType(Long classificationType) {
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

  public ContentionDetail diagnosticTypeCode(String diagnosticTypeCode) {
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

  public ContentionDetail claimantText(String claimantText) {
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

  public ContentionDetail contentionStatusTypeCode(String contentionStatusTypeCode) {
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

  public ContentionDetail originalSourceTypeCode(String originalSourceTypeCode) {
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

  public ContentionDetail specialIssueCodes(List<String> specialIssueCodes) {
    this.specialIssueCodes = specialIssueCodes;
    return this;
  }

  /**
   * adds special issue code.
   *
   * @param specialIssueCodesItem special issue code
   * @return contention detail
   */
  public ContentionDetail addSpecialIssueCodesItem(String specialIssueCodesItem) {
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

  /**
   * Retrieves associated tracked items.
   *
   * @param associatedTrackedItems tracked items.
   * @return contention detial
   */
  public ContentionDetail associatedTrackedItems(
      List<TrackedItemAssociationsOnContention> associatedTrackedItems) {
    this.associatedTrackedItems = associatedTrackedItems;
    return this;
  }

  /**
   * Adds a tracked item.
   *
   * @param associatedTrackedItemsItem associated item
   * @return ceontention
   */
  public ContentionDetail addAssociatedTrackedItemsItem(
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

  public ContentionDetail contentionId(Long contentionId) {
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

  public ContentionDetail lastModified(OffsetDateTime lastModified) {
    this.lastModified = lastModified;
    return this;
  }

  /**
   * Last modified time for the contention being updated. If this does not match, the update will be
   * rejected.
   *
   * @return lastModified
   */
  @NotNull
  @Valid
  @Schema(
      name = "lastModified",
      description =
          """
          Last modified time for the contention being updated. If this does not match, the
          update will be rejected.
          """)
  public OffsetDateTime getLastModified() {
    return lastModified;
  }

  public void setLastModified(OffsetDateTime lastModified) {
    this.lastModified = lastModified;
  }

  public ContentionDetail lifecycleStatus(String lifecycleStatus) {
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

  public ContentionDetail action(String action) {
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

  public ContentionDetail automationIndicator(Boolean automationIndicator) {
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

  public ContentionDetail summaryDateTime(OffsetDateTime summaryDateTime) {
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

  public ContentionDetail contentionHistory(List<ContentionHistory> contentionHistory) {
    this.contentionHistory = contentionHistory;
    return this;
  }

  /**
   * Adds contention history.
   *
   * @param contentionHistoryItem contention history
   * @return contention detail
   */
  public ContentionDetail addContentionHistoryItem(ContentionHistory contentionHistoryItem) {
    if (this.contentionHistory == null) {
      this.contentionHistory = new ArrayList<>();
    }
    this.contentionHistory.add(contentionHistoryItem);
    return this;
  }

  /**
   * Get contentionHistory.
   *
   * @return contentionHistory
   */
  @Valid
  @Schema(name = "contentionHistory")
  public List<ContentionHistory> getContentionHistory() {
    return contentionHistory;
  }

  public void setContentionHistory(List<ContentionHistory> contentionHistory) {
    this.contentionHistory = contentionHistory;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentionDetail contentionDetail = (ContentionDetail) o;
    return Objects.equals(this.medicalInd, contentionDetail.medicalInd)
        && Objects.equals(this.beginDate, contentionDetail.beginDate)
        && Objects.equals(this.createDate, contentionDetail.createDate)
        && Objects.equals(this.altContentionName, contentionDetail.altContentionName)
        && Objects.equals(this.completedDate, contentionDetail.completedDate)
        && Objects.equals(this.notificationDate, contentionDetail.notificationDate)
        && Objects.equals(this.contentionTypeCode, contentionDetail.contentionTypeCode)
        && Objects.equals(this.classificationType, contentionDetail.classificationType)
        && Objects.equals(this.diagnosticTypeCode, contentionDetail.diagnosticTypeCode)
        && Objects.equals(this.claimantText, contentionDetail.claimantText)
        && Objects.equals(this.contentionStatusTypeCode, contentionDetail.contentionStatusTypeCode)
        && Objects.equals(this.originalSourceTypeCode, contentionDetail.originalSourceTypeCode)
        && Objects.equals(this.specialIssueCodes, contentionDetail.specialIssueCodes)
        && Objects.equals(this.associatedTrackedItems, contentionDetail.associatedTrackedItems)
        && Objects.equals(this.contentionId, contentionDetail.contentionId)
        && Objects.equals(this.lastModified, contentionDetail.lastModified)
        && Objects.equals(this.lifecycleStatus, contentionDetail.lifecycleStatus)
        && Objects.equals(this.action, contentionDetail.action)
        && Objects.equals(this.automationIndicator, contentionDetail.automationIndicator)
        && Objects.equals(this.summaryDateTime, contentionDetail.summaryDateTime)
        && Objects.equals(this.contentionHistory, contentionDetail.contentionHistory);
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
        summaryDateTime,
        contentionHistory);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentionDetail {\n");
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
    sb.append("    contentionHistory: ").append(toIndentedString(contentionHistory)).append("\n");
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
