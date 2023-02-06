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

/**
 * A condition or diagnosis that a Veteran contends are the cause of a current disability, and may
 * qualify for benefits, if directly related to a Veteran&#39;s military service.
 */
@Schema(
    name = "Contention",
    description =
        """
        A condition or diagnosis that a Veteran contends are the cause of a current disability,
        and may qualify for benefits, if directly related to a Veteran's military service.
        """)
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class Contention {

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

  public Contention medicalInd(Boolean medicalInd) {
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

  public Contention beginDate(OffsetDateTime beginDate) {
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

  public Contention createDate(OffsetDateTime createDate) {
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

  public Contention altContentionName(String altContentionName) {
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

  public Contention completedDate(OffsetDateTime completedDate) {
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

  public Contention notificationDate(OffsetDateTime notificationDate) {
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

  public Contention contentionTypeCode(String contentionTypeCode) {
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

  public Contention classificationType(Long classificationType) {
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

  public Contention diagnosticTypeCode(String diagnosticTypeCode) {
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

  public Contention claimantText(String claimantText) {
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

  public Contention contentionStatusTypeCode(String contentionStatusTypeCode) {
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

  public Contention originalSourceTypeCode(String originalSourceTypeCode) {
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

  public Contention specialIssueCodes(List<String> specialIssueCodes) {
    this.specialIssueCodes = specialIssueCodes;
    return this;
  }

  /**
   * Adds a special issue codes item.
   *
   * @param specialIssueCodesItem special issue code
   * @return contention
   */
  public Contention addSpecialIssueCodesItem(String specialIssueCodesItem) {
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
   * Retrieves tracked items.
   *
   * @param associatedTrackedItems tracked item
   * @return contention
   */
  public Contention associatedTrackedItems(
      List<TrackedItemAssociationsOnContention> associatedTrackedItems) {
    this.associatedTrackedItems = associatedTrackedItems;
    return this;
  }

  /**
   * Retrieves traacked items.
   *
   * @param associatedTrackedItemsItem associated tracked items
   * @return Contention
   */
  public Contention addAssociatedTrackedItemsItem(
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Contention contention = (Contention) o;
    return Objects.equals(this.medicalInd, contention.medicalInd)
        && Objects.equals(this.beginDate, contention.beginDate)
        && Objects.equals(this.createDate, contention.createDate)
        && Objects.equals(this.altContentionName, contention.altContentionName)
        && Objects.equals(this.completedDate, contention.completedDate)
        && Objects.equals(this.notificationDate, contention.notificationDate)
        && Objects.equals(this.contentionTypeCode, contention.contentionTypeCode)
        && Objects.equals(this.classificationType, contention.classificationType)
        && Objects.equals(this.diagnosticTypeCode, contention.diagnosticTypeCode)
        && Objects.equals(this.claimantText, contention.claimantText)
        && Objects.equals(this.contentionStatusTypeCode, contention.contentionStatusTypeCode)
        && Objects.equals(this.originalSourceTypeCode, contention.originalSourceTypeCode)
        && Objects.equals(this.specialIssueCodes, contention.specialIssueCodes)
        && Objects.equals(this.associatedTrackedItems, contention.associatedTrackedItems);
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
        associatedTrackedItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Contention {\n");
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
