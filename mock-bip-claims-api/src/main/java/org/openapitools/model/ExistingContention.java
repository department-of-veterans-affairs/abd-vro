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

/** ExistingContention. */
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ExistingContention {

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

  public ExistingContention medicalInd(Boolean medicalInd) {
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

  public ExistingContention beginDate(OffsetDateTime beginDate) {
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

  public ExistingContention createDate(OffsetDateTime createDate) {
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

  public ExistingContention altContentionName(String altContentionName) {
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

  public ExistingContention completedDate(OffsetDateTime completedDate) {
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

  public ExistingContention notificationDate(OffsetDateTime notificationDate) {
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

  public ExistingContention contentionTypeCode(String contentionTypeCode) {
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

  public ExistingContention classificationType(Long classificationType) {
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

  public ExistingContention diagnosticTypeCode(String diagnosticTypeCode) {
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

  public ExistingContention claimantText(String claimantText) {
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

  public ExistingContention contentionStatusTypeCode(String contentionStatusTypeCode) {
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

  public ExistingContention originalSourceTypeCode(String originalSourceTypeCode) {
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

  public ExistingContention specialIssueCodes(List<String> specialIssueCodes) {
    this.specialIssueCodes = specialIssueCodes;
    return this;
  }

  /**
   * Adds a special issue code.
   *
   * @param specialIssueCodesItem specidal issue item
   * @return existing contention
   */
  public ExistingContention addSpecialIssueCodesItem(String specialIssueCodesItem) {
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
   * Retrieves existing contention.
   *
   * @param associatedTrackedItems tracked items
   * @return existing contention
   */
  public ExistingContention associatedTrackedItems(
      List<TrackedItemAssociationsOnContention> associatedTrackedItems) {
    this.associatedTrackedItems = associatedTrackedItems;
    return this;
  }

  /**
   * Retrieves exustubg contentions.
   *
   * @param associatedTrackedItemsItem tracked ite,
   * @return contention
   */
  public ExistingContention addAssociatedTrackedItemsItem(
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

  public ExistingContention contentionId(Long contentionId) {
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

  public ExistingContention lastModified(OffsetDateTime lastModified) {
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

  public ExistingContention lifecycleStatus(String lifecycleStatus) {
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

  public ExistingContention action(String action) {
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

  public ExistingContention automationIndicator(Boolean automationIndicator) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExistingContention existingContention = (ExistingContention) o;
    return Objects.equals(this.medicalInd, existingContention.medicalInd)
        && Objects.equals(this.beginDate, existingContention.beginDate)
        && Objects.equals(this.createDate, existingContention.createDate)
        && Objects.equals(this.altContentionName, existingContention.altContentionName)
        && Objects.equals(this.completedDate, existingContention.completedDate)
        && Objects.equals(this.notificationDate, existingContention.notificationDate)
        && Objects.equals(this.contentionTypeCode, existingContention.contentionTypeCode)
        && Objects.equals(this.classificationType, existingContention.classificationType)
        && Objects.equals(this.diagnosticTypeCode, existingContention.diagnosticTypeCode)
        && Objects.equals(this.claimantText, existingContention.claimantText)
        && Objects.equals(
            this.contentionStatusTypeCode, existingContention.contentionStatusTypeCode)
        && Objects.equals(this.originalSourceTypeCode, existingContention.originalSourceTypeCode)
        && Objects.equals(this.specialIssueCodes, existingContention.specialIssueCodes)
        && Objects.equals(this.associatedTrackedItems, existingContention.associatedTrackedItems)
        && Objects.equals(this.contentionId, existingContention.contentionId)
        && Objects.equals(this.lastModified, existingContention.lastModified)
        && Objects.equals(this.lifecycleStatus, existingContention.lifecycleStatus)
        && Objects.equals(this.action, existingContention.action)
        && Objects.equals(this.automationIndicator, existingContention.automationIndicator);
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
        automationIndicator);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExistingContention {\n");
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
