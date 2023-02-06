package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** UpdateClaimRequest. */
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class UpdateClaimRequest {

  @JsonProperty("suspenseReasonCode")
  private String suspenseReasonCode;

  @JsonProperty("suspenseDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime suspenseDate;

  @JsonProperty("commentText")
  private String commentText;

  public UpdateClaimRequest suspenseReasonCode(String suspenseReasonCode) {
    this.suspenseReasonCode = suspenseReasonCode;
    return this;
  }

  /**
   * Get suspenseReasonCode.
   *
   * @return suspenseReasonCode
   */
  @Schema(name = "suspenseReasonCode", example = "024")
  public String getSuspenseReasonCode() {
    return suspenseReasonCode;
  }

  public void setSuspenseReasonCode(String suspenseReasonCode) {
    this.suspenseReasonCode = suspenseReasonCode;
  }

  public UpdateClaimRequest suspenseDate(OffsetDateTime suspenseDate) {
    this.suspenseDate = suspenseDate;
    return this;
  }

  /**
   * Get suspenseDate.
   *
   * @return suspenseDate
   */
  @Valid
  @Schema(name = "suspenseDate")
  public OffsetDateTime getSuspenseDate() {
    return suspenseDate;
  }

  public void setSuspenseDate(OffsetDateTime suspenseDate) {
    this.suspenseDate = suspenseDate;
  }

  public UpdateClaimRequest commentText(String commentText) {
    this.commentText = commentText;
    return this;
  }

  /**
   * Get commentText.
   *
   * @return commentText
   */
  @Schema(name = "commentText", example = "Adding custom text")
  public String getCommentText() {
    return commentText;
  }

  public void setCommentText(String commentText) {
    this.commentText = commentText;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateClaimRequest updateClaimRequest = (UpdateClaimRequest) o;
    return Objects.equals(this.suspenseReasonCode, updateClaimRequest.suspenseReasonCode)
        && Objects.equals(this.suspenseDate, updateClaimRequest.suspenseDate)
        && Objects.equals(this.commentText, updateClaimRequest.commentText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(suspenseReasonCode, suspenseDate, commentText);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateClaimRequest {\n");
    sb.append("    suspenseReasonCode: ").append(toIndentedString(suspenseReasonCode)).append("\n");
    sb.append("    suspenseDate: ").append(toIndentedString(suspenseDate)).append("\n");
    sb.append("    commentText: ").append(toIndentedString(commentText)).append("\n");
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
