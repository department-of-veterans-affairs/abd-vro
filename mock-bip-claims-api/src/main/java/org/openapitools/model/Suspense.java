package org.openapitools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/**
 * The time during which a work item or claim is inactive or waiting completion of an action from an
 * outside entity, such as waiting for evidence requested from the claimant or a third party.
 * Receipt of the evidence will lift the suspense and allow the claim to move forward through
 * processing.
 */
@Schema(
    name = "Suspense",
    description =
        """
        The time during which a work item or claim is inactive or waiting completion of an
        action from an outside entity, such as waiting for evidence requested from the claimant
        or a third party. Receipt of the evidence will lift the suspense and allow the claim to
        move forward through processing.
        """)
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class Suspense {

  @JsonProperty("reason")
  private String reason;

  @JsonProperty("reasonCode")
  private String reasonCode;

  @JsonProperty("date")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime date;

  @JsonProperty("comment")
  private String comment;

  @JsonProperty("changedDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime changedDate;

  @JsonProperty("changedBy")
  private String changedBy;

  public Suspense reason(String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * The explanation regarding a pause in claim processing, such as waiting for a response to an
   * information request. Many claim actions have built-in suspense reasons that are either
   * automatically recorded, or selected from a list by the user.
   *
   * @return reason
   */
  @Schema(
      name = "reason",
      example = "Pending Authorization",
      description =
          """
          The explanation regarding a pause in claim processing, such as waiting for a response
          to an information request. Many claim actions have built-in suspense reasons that are
          either automatically recorded, or selected from a list by the user.
          """)
  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public Suspense reasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
    return this;
  }

  /**
   * The suspense reason code.
   *
   * @return reasonCode
   */
  @Schema(name = "reasonCode", example = "024", description = "The suspense reason code.")
  public String getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
  }

  public Suspense date(OffsetDateTime date) {
    this.date = date;
    return this;
  }

  /**
   * Acts as a due date or reminder date for either a tracked item or a claim. The claim suspense
   * date will usually be the same as the suspense date of the earliest, actionable tracked item.
   *
   * @return date
   */
  @Valid
  @Schema(
      name = "date",
      description =
          """
          Acts as a due date or reminder date for either a tracked item or a claim. The claim
          suspense date will usually be the same as the suspense date of the earliest, actionable
          tracked item.
          """)
  public OffsetDateTime getDate() {
    return date;
  }

  public void setDate(OffsetDateTime date) {
    this.date = date;
  }

  public Suspense comment(String comment) {
    this.comment = comment;
    return this;
  }

  /**
   * Get comment.
   *
   * @return comment
   */
  @Schema(name = "comment", example = "Suspense Comment Example")
  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Suspense changedDate(OffsetDateTime changedDate) {
    this.changedDate = changedDate;
    return this;
  }

  /**
   * The date of this change to the suspense.
   *
   * @return changedDate
   */
  @Valid
  @Schema(name = "changedDate", description = "The date of this change to the suspense.")
  public OffsetDateTime getChangedDate() {
    return changedDate;
  }

  public void setChangedDate(OffsetDateTime changedDate) {
    this.changedDate = changedDate;
  }

  public Suspense changedBy(String changedBy) {
    this.changedBy = changedBy;
    return this;
  }

  /**
   * The userId of the user who changed the suspense.
   *
   * @return changedBy
   */
  @Schema(
      name = "changedBy",
      example = "user123",
      description = "The userId of the user who changed the suspense.")
  public String getChangedBy() {
    return changedBy;
  }

  public void setChangedBy(String changedBy) {
    this.changedBy = changedBy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Suspense suspense = (Suspense) o;
    return Objects.equals(this.reason, suspense.reason)
        && Objects.equals(this.reasonCode, suspense.reasonCode)
        && Objects.equals(this.date, suspense.date)
        && Objects.equals(this.comment, suspense.comment)
        && Objects.equals(this.changedDate, suspense.changedDate)
        && Objects.equals(this.changedBy, suspense.changedBy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reason, reasonCode, date, comment, changedDate, changedBy);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Suspense {\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    reasonCode: ").append(toIndentedString(reasonCode)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
    sb.append("    changedDate: ").append(toIndentedString(changedDate)).append("\n");
    sb.append("    changedBy: ").append(toIndentedString(changedBy)).append("\n");
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
