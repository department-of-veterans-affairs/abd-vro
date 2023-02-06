package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.NotNull;

/** UpdateContentionAutomationStatusRequestItem. */
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class UpdateContentionAutomationStatusRequestItem {

  @JsonProperty("contentionId")
  private Long contentionId;

  @JsonProperty("automationIndicator")
  private Boolean automationIndicator;

  @JsonProperty("details")
  private String details;

  public UpdateContentionAutomationStatusRequestItem contentionId(Long contentionId) {
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

  public UpdateContentionAutomationStatusRequestItem automationIndicator(
      Boolean automationIndicator) {
    this.automationIndicator = automationIndicator;
    return this;
  }

  /**
   * The automation status of the contention.
   *
   * @return automationIndicator
   */
  @NotNull
  @Schema(name = "automationIndicator", description = "The automation status of the contention.")
  public Boolean getAutomationIndicator() {
    return automationIndicator;
  }

  public void setAutomationIndicator(Boolean automationIndicator) {
    this.automationIndicator = automationIndicator;
  }

  public UpdateContentionAutomationStatusRequestItem details(String details) {
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
    UpdateContentionAutomationStatusRequestItem updateContentionAutomationStatusRequestItem =
        (UpdateContentionAutomationStatusRequestItem) o;
    return Objects.equals(
            this.contentionId, updateContentionAutomationStatusRequestItem.contentionId)
        && Objects.equals(
            this.automationIndicator,
            updateContentionAutomationStatusRequestItem.automationIndicator)
        && Objects.equals(this.details, updateContentionAutomationStatusRequestItem.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contentionId, automationIndicator, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateContentionAutomationStatusRequestItem {\n");
    sb.append("    contentionId: ").append(toIndentedString(contentionId)).append("\n");
    sb.append("    automationIndicator: ")
        .append(toIndentedString(automationIndicator))
        .append("\n");
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
