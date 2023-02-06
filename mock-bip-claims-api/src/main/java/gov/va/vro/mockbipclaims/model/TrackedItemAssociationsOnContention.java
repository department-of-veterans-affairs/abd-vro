package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.NotNull;

/** The association between a contention and tracked item. */
@Schema(
    name = "TrackedItemAssociationsOnContention",
    description = "The association between a contention and tracked item.")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class TrackedItemAssociationsOnContention {

  @JsonProperty("trackedItemId")
  private Long trackedItemId;

  public TrackedItemAssociationsOnContention trackedItemId(Long trackedItemId) {
    this.trackedItemId = trackedItemId;
    return this;
  }

  /**
   * Get trackedItemId.
   *
   * @return trackedItemId
   */
  @NotNull
  @Schema(name = "trackedItemId", example = "5678")
  public Long getTrackedItemId() {
    return trackedItemId;
  }

  public void setTrackedItemId(Long trackedItemId) {
    this.trackedItemId = trackedItemId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TrackedItemAssociationsOnContention trackedItemAssociationsOnContention =
        (TrackedItemAssociationsOnContention) o;
    return Objects.equals(this.trackedItemId, trackedItemAssociationsOnContention.trackedItemId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(trackedItemId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TrackedItemAssociationsOnContention {\n");
    sb.append("    trackedItemId: ").append(toIndentedString(trackedItemId)).append("\n");
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
