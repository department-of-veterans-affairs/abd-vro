package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/** ExistingContentionAllOf. */
@JsonTypeName("ExistingContention_allOf")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ExistingContentionAllOf {

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

  public ExistingContentionAllOf contentionId(Long contentionId) {
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

  public ExistingContentionAllOf lastModified(OffsetDateTime lastModified) {
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
          Last modified time for the contention being updated. If this does not match, the update
          will be rejected.
          """)
  public OffsetDateTime getLastModified() {
    return lastModified;
  }

  public void setLastModified(OffsetDateTime lastModified) {
    this.lastModified = lastModified;
  }

  public ExistingContentionAllOf lifecycleStatus(String lifecycleStatus) {
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

  public ExistingContentionAllOf action(String action) {
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

  public ExistingContentionAllOf automationIndicator(Boolean automationIndicator) {
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
    ExistingContentionAllOf existingContentionAllOf = (ExistingContentionAllOf) o;
    return Objects.equals(this.contentionId, existingContentionAllOf.contentionId)
        && Objects.equals(this.lastModified, existingContentionAllOf.lastModified)
        && Objects.equals(this.lifecycleStatus, existingContentionAllOf.lifecycleStatus)
        && Objects.equals(this.action, existingContentionAllOf.action)
        && Objects.equals(this.automationIndicator, existingContentionAllOf.automationIndicator);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contentionId, lastModified, lifecycleStatus, action, automationIndicator);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExistingContentionAllOf {\n");
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
