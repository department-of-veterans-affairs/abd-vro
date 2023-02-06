package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import javax.annotation.Generated;

/** UpdateClaimLifecycleStatusRequest. */
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class UpdateClaimLifecycleStatusRequest {

  @JsonProperty("claimLifecycleStatus")
  private String claimLifecycleStatus;

  public UpdateClaimLifecycleStatusRequest claimLifecycleStatus(String claimLifecycleStatus) {
    this.claimLifecycleStatus = claimLifecycleStatus;
    return this;
  }

  /**
   * Get claimLifecycleStatus.
   *
   * @return claimLifecycleStatus
   */
  @Schema(name = "claimLifecycleStatus", example = "Rating Decision Complete")
  public String getClaimLifecycleStatus() {
    return claimLifecycleStatus;
  }

  public void setClaimLifecycleStatus(String claimLifecycleStatus) {
    this.claimLifecycleStatus = claimLifecycleStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateClaimLifecycleStatusRequest updateClaimLifecycleStatusRequest =
        (UpdateClaimLifecycleStatusRequest) o;
    return Objects.equals(
        this.claimLifecycleStatus, updateClaimLifecycleStatusRequest.claimLifecycleStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(claimLifecycleStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateClaimLifecycleStatusRequest {\n");
    sb.append("    claimLifecycleStatus: ")
        .append(toIndentedString(claimLifecycleStatus))
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
