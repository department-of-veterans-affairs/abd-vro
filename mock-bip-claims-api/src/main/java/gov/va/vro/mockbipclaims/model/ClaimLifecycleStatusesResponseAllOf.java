package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** ClaimLifecycleStatusesResponseAllOf. */
@JsonTypeName("ClaimLifecycleStatusesResponse_allOf")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ClaimLifecycleStatusesResponseAllOf {

  @JsonProperty("lifecycleStatuses")
  @Valid
  private List<Lifecycle> lifecycleStatuses = null;

  public ClaimLifecycleStatusesResponseAllOf lifecycleStatuses(List<Lifecycle> lifecycleStatuses) {
    this.lifecycleStatuses = lifecycleStatuses;
    return this;
  }

  /**
   * Add lifecycle status.
   *
   * @param lifecycleStatusesItem lifecycle status
   * @return Statuses
   */
  public ClaimLifecycleStatusesResponseAllOf addLifecycleStatusesItem(
      Lifecycle lifecycleStatusesItem) {
    if (this.lifecycleStatuses == null) {
      this.lifecycleStatuses = new ArrayList<>();
    }
    this.lifecycleStatuses.add(lifecycleStatusesItem);
    return this;
  }

  /**
   * Get lifecycleStatuses.
   *
   * @return lifecycleStatuses
   */
  @Valid
  @Schema(name = "lifecycleStatuses")
  public List<Lifecycle> getLifecycleStatuses() {
    return lifecycleStatuses;
  }

  public void setLifecycleStatuses(List<Lifecycle> lifecycleStatuses) {
    this.lifecycleStatuses = lifecycleStatuses;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClaimLifecycleStatusesResponseAllOf claimLifecycleStatusesResponseAllOf =
        (ClaimLifecycleStatusesResponseAllOf) o;
    return Objects.equals(
        this.lifecycleStatuses, claimLifecycleStatusesResponseAllOf.lifecycleStatuses);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lifecycleStatuses);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClaimLifecycleStatusesResponseAllOf {\n");
    sb.append("    lifecycleStatuses: ").append(toIndentedString(lifecycleStatuses)).append("\n");
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
