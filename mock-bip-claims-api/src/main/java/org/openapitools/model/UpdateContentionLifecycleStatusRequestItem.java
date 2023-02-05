package org.openapitools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import javax.annotation.Generated;

/** UpdateContentionLifecycleStatusRequestItem. */
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class UpdateContentionLifecycleStatusRequestItem {

  @JsonProperty("contentionId")
  private Long contentionId;

  @JsonProperty("lifecycleStatus")
  private String lifecycleStatus;

  @JsonProperty("details")
  private String details;

  public UpdateContentionLifecycleStatusRequestItem contentionId(Long contentionId) {
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

  public UpdateContentionLifecycleStatusRequestItem lifecycleStatus(String lifecycleStatus) {
    this.lifecycleStatus = lifecycleStatus;
    return this;
  }

  /**
   * The lifecycle status of the contention. See
   * [/contentions/lifecycle_status](#/lookup/getContentionLifecycleStatuses) for valid values. The
   * value for code or description can be used.
   *
   * @return lifecycleStatus
   */
  @Schema(
      name = "lifecycleStatus",
      example = "Open",
      description =
          """
          The lifecycle status of the contention.
          See [/contentions/lifecycle_status](#/lookup/getContentionLifecycleStatuses)
          for valid values. The value for code or description can be used.
          """)
  public String getLifecycleStatus() {
    return lifecycleStatus;
  }

  public void setLifecycleStatus(String lifecycleStatus) {
    this.lifecycleStatus = lifecycleStatus;
  }

  public UpdateContentionLifecycleStatusRequestItem details(String details) {
    this.details = details;
    return this;
  }

  /**
   * User provided details of the action. A maximum of 400 characters is allowed.
   *
   * @return details
   */
  @Schema(
      name = "details",
      description = "User provided details of the action. A maximum of 400 characters is allowed.")
  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateContentionLifecycleStatusRequestItem updateContentionLifecycleStatusRequestItem =
        (UpdateContentionLifecycleStatusRequestItem) o;
    return Objects.equals(
            this.contentionId, updateContentionLifecycleStatusRequestItem.contentionId)
        && Objects.equals(
            this.lifecycleStatus, updateContentionLifecycleStatusRequestItem.lifecycleStatus)
        && Objects.equals(this.details, updateContentionLifecycleStatusRequestItem.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contentionId, lifecycleStatus, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateContentionLifecycleStatusRequestItem {\n");
    sb.append("    contentionId: ").append(toIndentedString(contentionId)).append("\n");
    sb.append("    lifecycleStatus: ").append(toIndentedString(lifecycleStatus)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
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
