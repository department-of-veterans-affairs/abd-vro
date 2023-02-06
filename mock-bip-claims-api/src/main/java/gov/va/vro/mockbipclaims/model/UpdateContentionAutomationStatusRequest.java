package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/** Request to update contention automation status. */
@Schema(
    name = "UpdateContentionAutomationStatusRequest",
    description = "Request to update contention automation status.")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class UpdateContentionAutomationStatusRequest {

  @JsonProperty("contentions")
  @Valid
  private List<UpdateContentionAutomationStatusRequestItem> contentions = new ArrayList<>();

  public UpdateContentionAutomationStatusRequest contentions(
      List<UpdateContentionAutomationStatusRequestItem> contentions) {
    this.contentions = contentions;
    return this;
  }

  public UpdateContentionAutomationStatusRequest addContentionsItem(
      UpdateContentionAutomationStatusRequestItem contentionsItem) {
    this.contentions.add(contentionsItem);
    return this;
  }

  /**
   * Get contentions.
   *
   * @return contentions
   */
  @NotNull
  @Valid
  @Size(min = 1)
  @Schema(name = "contentions")
  public List<UpdateContentionAutomationStatusRequestItem> getContentions() {
    return contentions;
  }

  public void setContentions(List<UpdateContentionAutomationStatusRequestItem> contentions) {
    this.contentions = contentions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateContentionAutomationStatusRequest updateContentionAutomationStatusRequest =
        (UpdateContentionAutomationStatusRequest) o;
    return Objects.equals(this.contentions, updateContentionAutomationStatusRequest.contentions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contentions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateContentionAutomationStatusRequest {\n");
    sb.append("    contentions: ").append(toIndentedString(contentions)).append("\n");
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
