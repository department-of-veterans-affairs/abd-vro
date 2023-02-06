package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** Status updates for the lifecycle of a claim. */
@Schema(name = "Lifecycle", description = "Status updates for the lifecycle of a claim")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class Lifecycle {

  @JsonProperty("lifecycleStatusTypeCode")
  private String lifecycleStatusTypeCode;

  @JsonProperty("lifecycleStatusTypeName")
  private String lifecycleStatusTypeName;

  @JsonProperty("lifecycleStatusReasonTypeCode")
  private String lifecycleStatusReasonTypeCode;

  @JsonProperty("lifecycleStatusReasonTypeName")
  private String lifecycleStatusReasonTypeName;

  @JsonProperty("reasonDetailTypeCode")
  private String reasonDetailTypeCode;

  @JsonProperty("reasonDetailTypeName")
  private String reasonDetailTypeName;

  @JsonProperty("reasonText")
  private String reasonText;

  @JsonProperty("changedDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime changedDate;

  @JsonProperty("closedDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime closedDate;

  public Lifecycle lifecycleStatusTypeCode(String lifecycleStatusTypeCode) {
    this.lifecycleStatusTypeCode = lifecycleStatusTypeCode;
    return this;
  }

  /**
   * Type code corresponding to BNFT_CLAIM_LC_STATUS_TYPE group type name in CorpDB.
   *
   * @return lifecycleStatusTypeCode
   */
  @Schema(
      name = "lifecycleStatusTypeCode",
      example = "RFD",
      description =
          "Type code corresponding to BNFT_CLAIM_LC_STATUS_TYPE group type name in CorpDB")
  public String getLifecycleStatusTypeCode() {
    return lifecycleStatusTypeCode;
  }

  public void setLifecycleStatusTypeCode(String lifecycleStatusTypeCode) {
    this.lifecycleStatusTypeCode = lifecycleStatusTypeCode;
  }

  public Lifecycle lifecycleStatusTypeName(String lifecycleStatusTypeName) {
    this.lifecycleStatusTypeName = lifecycleStatusTypeName;
    return this;
  }

  /**
   * Type name corresponding to BNFT_CLAIM_LC_STATUS_TYPE group type name in CorpDB.
   *
   * @return lifecycleStatusTypeName
   */
  @Schema(
      name = "lifecycleStatusTypeName",
      example = "Ready for Decision",
      description =
          "Type name corresponding to BNFT_CLAIM_LC_STATUS_TYPE group type name in CorpDB")
  public String getLifecycleStatusTypeName() {
    return lifecycleStatusTypeName;
  }

  public void setLifecycleStatusTypeName(String lifecycleStatusTypeName) {
    this.lifecycleStatusTypeName = lifecycleStatusTypeName;
  }

  public Lifecycle lifecycleStatusReasonTypeCode(String lifecycleStatusReasonTypeCode) {
    this.lifecycleStatusReasonTypeCode = lifecycleStatusReasonTypeCode;
    return this;
  }

  /**
   * Type code corresponding to LC_STATUS_REASON_TYPE group type name in CorpDB.
   *
   * @return lifecycleStatusReasonTypeCode
   */
  @Schema(
      name = "lifecycleStatusReasonTypeCode",
      example = "54",
      description = "Type code corresponding to LC_STATUS_REASON_TYPE group type name in CorpDB")
  public String getLifecycleStatusReasonTypeCode() {
    return lifecycleStatusReasonTypeCode;
  }

  public void setLifecycleStatusReasonTypeCode(String lifecycleStatusReasonTypeCode) {
    this.lifecycleStatusReasonTypeCode = lifecycleStatusReasonTypeCode;
  }

  public Lifecycle lifecycleStatusReasonTypeName(String lifecycleStatusReasonTypeName) {
    this.lifecycleStatusReasonTypeName = lifecycleStatusReasonTypeName;
    return this;
  }

  /**
   * Type name corresponding to LC_STATUS_REASON_TYPE group type name in CorpDB.
   *
   * @return lifecycleStatusReasonTypeName
   */
  @Schema(
      name = "lifecycleStatusReasonTypeName",
      example = "Worked in VETSNET Awards",
      description = "Type name corresponding to LC_STATUS_REASON_TYPE group type name in CorpDB")
  public String getLifecycleStatusReasonTypeName() {
    return lifecycleStatusReasonTypeName;
  }

  public void setLifecycleStatusReasonTypeName(String lifecycleStatusReasonTypeName) {
    this.lifecycleStatusReasonTypeName = lifecycleStatusReasonTypeName;
  }

  public Lifecycle reasonDetailTypeCode(String reasonDetailTypeCode) {
    this.reasonDetailTypeCode = reasonDetailTypeCode;
    return this;
  }

  /**
   * Type code corresponding to REASON_DETAIL_TYPE group type name in CorpDB.
   *
   * @return reasonDetailTypeCode
   */
  @Schema(
      name = "reasonDetailTypeCode",
      example = "03B",
      description = "Type code corresponding to REASON_DETAIL_TYPE group type name in CorpDB")
  public String getReasonDetailTypeCode() {
    return reasonDetailTypeCode;
  }

  public void setReasonDetailTypeCode(String reasonDetailTypeCode) {
    this.reasonDetailTypeCode = reasonDetailTypeCode;
  }

  public Lifecycle reasonDetailTypeName(String reasonDetailTypeName) {
    this.reasonDetailTypeName = reasonDetailTypeName;
    return this;
  }

  /**
   * Type name corresponding to REASON_DETAIL_TYPE group type name in CorpDB.
   *
   * @return reasonDetailTypeName
   */
  @Schema(
      name = "reasonDetailTypeName",
      example = "Financial Issues",
      description = "Type name corresponding to REASON_DETAIL_TYPE group type name in CorpDB")
  public String getReasonDetailTypeName() {
    return reasonDetailTypeName;
  }

  public void setReasonDetailTypeName(String reasonDetailTypeName) {
    this.reasonDetailTypeName = reasonDetailTypeName;
  }

  public Lifecycle reasonText(String reasonText) {
    this.reasonText = reasonText;
    return this;
  }

  /**
   * Free-form LC status update reason.
   *
   * @return reasonText
   */
  @Schema(
      name = "reasonText",
      example = "Reason Text",
      description = "Free-form LC status update reason")
  public String getReasonText() {
    return reasonText;
  }

  public void setReasonText(String reasonText) {
    this.reasonText = reasonText;
  }

  public Lifecycle changedDate(OffsetDateTime changedDate) {
    this.changedDate = changedDate;
    return this;
  }

  /**
   * Date of the LC status update for the claim.
   *
   * @return changedDate
   */
  @Valid
  @Schema(name = "changedDate", description = "Date of the LC status update for the claim")
  public OffsetDateTime getChangedDate() {
    return changedDate;
  }

  public void setChangedDate(OffsetDateTime changedDate) {
    this.changedDate = changedDate;
  }

  public Lifecycle closedDate(OffsetDateTime closedDate) {
    this.closedDate = closedDate;
    return this;
  }

  /**
   * Closed date for the claim.
   *
   * @return closedDate
   */
  @Valid
  @Schema(name = "closedDate", description = "Closed date for the claim")
  public OffsetDateTime getClosedDate() {
    return closedDate;
  }

  public void setClosedDate(OffsetDateTime closedDate) {
    this.closedDate = closedDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Lifecycle lifecycle = (Lifecycle) o;
    return Objects.equals(this.lifecycleStatusTypeCode, lifecycle.lifecycleStatusTypeCode)
        && Objects.equals(this.lifecycleStatusTypeName, lifecycle.lifecycleStatusTypeName)
        && Objects.equals(
            this.lifecycleStatusReasonTypeCode, lifecycle.lifecycleStatusReasonTypeCode)
        && Objects.equals(
            this.lifecycleStatusReasonTypeName, lifecycle.lifecycleStatusReasonTypeName)
        && Objects.equals(this.reasonDetailTypeCode, lifecycle.reasonDetailTypeCode)
        && Objects.equals(this.reasonDetailTypeName, lifecycle.reasonDetailTypeName)
        && Objects.equals(this.reasonText, lifecycle.reasonText)
        && Objects.equals(this.changedDate, lifecycle.changedDate)
        && Objects.equals(this.closedDate, lifecycle.closedDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        lifecycleStatusTypeCode,
        lifecycleStatusTypeName,
        lifecycleStatusReasonTypeCode,
        lifecycleStatusReasonTypeName,
        reasonDetailTypeCode,
        reasonDetailTypeName,
        reasonText,
        changedDate,
        closedDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Lifecycle {\n");
    sb.append("    lifecycleStatusTypeCode: ")
        .append(toIndentedString(lifecycleStatusTypeCode))
        .append("\n");
    sb.append("    lifecycleStatusTypeName: ")
        .append(toIndentedString(lifecycleStatusTypeName))
        .append("\n");
    sb.append("    lifecycleStatusReasonTypeCode: ")
        .append(toIndentedString(lifecycleStatusReasonTypeCode))
        .append("\n");
    sb.append("    lifecycleStatusReasonTypeName: ")
        .append(toIndentedString(lifecycleStatusReasonTypeName))
        .append("\n");
    sb.append("    reasonDetailTypeCode: ")
        .append(toIndentedString(reasonDetailTypeCode))
        .append("\n");
    sb.append("    reasonDetailTypeName: ")
        .append(toIndentedString(reasonDetailTypeName))
        .append("\n");
    sb.append("    reasonText: ").append(toIndentedString(reasonText)).append("\n");
    sb.append("    changedDate: ").append(toIndentedString(changedDate)).append("\n");
    sb.append("    closedDate: ").append(toIndentedString(closedDate)).append("\n");
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
